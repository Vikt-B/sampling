import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SampleClass {

    public static void main(String[] args) throws Exception {
        Program program = new Program();
        //endpoint data
        List<Measurement> unsampledMeasurements =  Data.unsampledMeasurements;
        LocalDateTime startOfSampling = LocalDateTime.parse("2017-01-03T10:00:00");
        //program
        Dictionary<MeasurementType, List<Measurement>> sampledMeasurements = program.sample(startOfSampling, unsampledMeasurements);
        //return
        List<Measurement> measurements = program.prettyPrint(sampledMeasurements);
    }
}

class Program {

    List<Measurement> prettyPrint(Dictionary<MeasurementType, List<Measurement>> sampledMeasurements){
        List<Measurement> returnList = new ArrayList<>();
        Enumeration<List<Measurement>> elements = sampledMeasurements.elements();
        while (elements.hasMoreElements()){
            returnList.addAll(elements.nextElement());
        }
        returnList = returnList.stream()
                .sorted(Comparator.comparing(Measurement::getType).thenComparing(Measurement::getMeasurementTime))
                .collect(Collectors.toList());
        System.out.println(returnList);

        return returnList;
    }

    Dictionary<MeasurementType, List<Measurement>> sample(LocalDateTime startOfSampling, List<Measurement> unsampledMeasurements) throws Exception {

        validateInputs(startOfSampling, unsampledMeasurements);
        Dictionary<MeasurementType, List<Measurement>> sampledMeasurements = new Hashtable<>();

        for (MeasurementType measurementType : MeasurementType.values()) {
            List<Measurement> collect = unsampledMeasurements.stream()
                    .filter(Objects::nonNull)
                    .filter(measurement -> measurementType.equals(measurement.getType()))
                    .collect(Collectors.toList());
            List<Measurement> measurements = sampleByType(collect, startOfSampling);
            if (!measurements.isEmpty()){
                sampledMeasurements.put(measurementType, measurements);
            }
        }

        return sampledMeasurements;
    }

    void validateInputs(LocalDateTime startOfSampling, List<Measurement> unsampledMeasurements) throws Exception {
         if (Objects.isNull(startOfSampling)){
             throw new Exception("Start of sample time not found");
         }
         if (Objects.isNull(unsampledMeasurements) || unsampledMeasurements.isEmpty()){
             throw new Exception("Measurements not found");
         }
    }

    List<Measurement> sampleByType(List<Measurement> unsampledMeasurements, LocalDateTime startOfSampling) {
        List<Measurement> sampledMeasurements = new ArrayList<>();

        getMeasurementMax(sampledMeasurements, unsampledMeasurements, startOfSampling);

        return sampledMeasurements.stream()
                .sorted(Comparator.comparing(Measurement::getMeasurementTime))
                .collect(Collectors.toList());
    }

    void getMeasurementMax(List<Measurement> sampledMeasurements, List<Measurement> unsampledMeasurements,
                           LocalDateTime sampleIntervalStart){

        LocalDateTime sampleIntervalEnd = sampleIntervalStart.plusMinutes(5);

        unsampledMeasurements.stream()
                .filter(sample -> isBetweenEndInclusive(sample.getMeasurementTime(), sampleIntervalStart, sampleIntervalEnd))
                .max(Comparator.comparing(Measurement::getMeasurementTime))
                .ifPresent(maxMeasurement -> addMeasurementAsSampled(maxMeasurement, sampledMeasurements, sampleIntervalEnd));

        if (unsampledMeasurements.stream().anyMatch(x -> x.getMeasurementTime().isAfter(sampleIntervalEnd))){
            getMeasurementMax(sampledMeasurements, unsampledMeasurements,sampleIntervalEnd);
        }
    }

    void addMeasurementAsSampled(Measurement unsampledMeasurement, List<Measurement> sampledMeasurements, LocalDateTime sampleIntervalEnd){
        sampledMeasurements.add(new Measurement(sampleIntervalEnd, unsampledMeasurement.getType(), unsampledMeasurement.getMeasurementValue()));
    }

    boolean isBetweenEndInclusive(LocalDateTime measurementTime, LocalDateTime sampleIntervalStart, LocalDateTime sampleIntervalEnd) {
        return measurementTime.isAfter(sampleIntervalStart) && (measurementTime.isEqual(sampleIntervalEnd) || measurementTime.isBefore(sampleIntervalEnd));
    }
}

class Measurement {

    private LocalDateTime measurementTime;
    private MeasurementType type;
    private Double measurementValue;

    public LocalDateTime getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(LocalDateTime measurementTime) {
        this.measurementTime = measurementTime;
    }

    public Double getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(Double measurementValue) {
        this.measurementValue = measurementValue;
    }

    public MeasurementType getType() {
        return type;
    }

    public void setType(MeasurementType type) {
        this.type = type;
    }

    public Measurement(LocalDateTime measurementTime, MeasurementType type, Double measurementValue) {
        this.measurementTime = measurementTime;
        this.type = type;
        this.measurementValue = measurementValue;
    }

    @Override
    public String toString() {
        return measurementTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," +
                type + "," +
                 measurementValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Measurement measurement = (Measurement) o;
        return measurementTime.equals(measurement.measurementTime) &&
                type.equals(measurement.type) &&
                measurementValue.equals(measurement.measurementValue);
    }
}

enum MeasurementType {
    TEMP,
    SPO2,
    HR;
}

class Data {
    public static List<Measurement> unsampledMeasurements = populateData();

    private static List<Measurement> populateData() {
        List<Measurement> data = new ArrayList<>();
        data.add(new Measurement(LocalDateTime.parse("2017-01-03T10:04:45"), MeasurementType.TEMP, 35.79));
        data.add(new Measurement(LocalDateTime.parse("2017-01-03T10:01:18"), MeasurementType.SPO2, 98.78));
        data.add(new Measurement(LocalDateTime.parse("2017-01-03T10:09:07"), MeasurementType.TEMP, 35.01));
        data.add(new Measurement(LocalDateTime.parse("2017-01-03T10:03:34"), MeasurementType.SPO2, 96.49));
        data.add(new Measurement(LocalDateTime.parse("2017-01-03T10:02:01"), MeasurementType.TEMP, 35.82));
        data.add(new Measurement(LocalDateTime.parse("2017-01-03T10:05:00"), MeasurementType.SPO2, 97.17));
        data.add(new Measurement(LocalDateTime.parse("2017-01-03T10:05:01"), MeasurementType.SPO2, 95.08));
        return data;
    }
}
