import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SampleClassTest {
    Program program = new Program();
    List<Measurement> unsampledMeasurementsData = new ArrayList<>();
    LocalDateTime startOfSampling;

    @BeforeEach
    void init(){
        unsampledMeasurementsData = Data.unsampledMeasurements;
        startOfSampling = LocalDateTime.parse("2017-01-03T10:00:00");
    }


    @Test
    void sample() throws Exception {
        List<Measurement> expectedReturnList = Arrays.asList(
                new Measurement(LocalDateTime.parse("2017-01-03T10:05:00"), MeasurementType.TEMP, 35.79),
                new Measurement(LocalDateTime.parse("2017-01-03T10:10:00"), MeasurementType.TEMP, 35.01),
                new Measurement(LocalDateTime.parse("2017-01-03T10:05:00"), MeasurementType.SPO2, 97.17),
                new Measurement(LocalDateTime.parse("2017-01-03T10:10:00"), MeasurementType.SPO2, 95.08));
        Dictionary<MeasurementType, List<Measurement>> sample = program.sample(startOfSampling, unsampledMeasurementsData);
        List<Measurement> measurements = program.prettyPrint(sample);
        assertEquals(measurements.size(), expectedReturnList.size());
        for (int i = 0; i < expectedReturnList.size(); i++) {
            assertEquals(measurements.get(i), expectedReturnList.get(i));
        }
    }

    @Test
    void prettyPrintTimeSort(){
        Dictionary <MeasurementType, List<Measurement>> unsortedTimeData = new Hashtable<>();
        unsortedTimeData.put(MeasurementType.TEMP ,Arrays.asList(
                new Measurement(LocalDateTime.parse("2017-01-03T10:15:00"), MeasurementType.TEMP, 35.79),
                new Measurement(LocalDateTime.parse("2017-01-03T10:10:00"), MeasurementType.TEMP, 35.01),
                new Measurement(LocalDateTime.parse("2017-01-03T10:45:00"), MeasurementType.TEMP, 35.17),
                new Measurement(LocalDateTime.parse("2017-01-03T10:05:00"), MeasurementType.TEMP, 35.08),
                new Measurement(LocalDateTime.parse("2017-01-03T10:25:00"), MeasurementType.TEMP, 35.09)));

        List<Measurement> measurements = program.prettyPrint(unsortedTimeData);

        boolean sorted = true;
        for(int i = 1; i < measurements.size(); i++){
            if(!measurements.get(i - 1).getMeasurementTime().isBefore(measurements.get(i).getMeasurementTime())){
                sorted = false;
            }
        }
        assertTrue(sorted);
    }

    @Test
    void sampleDateIsNull() {
        startOfSampling = null;
        Exception exception = assertThrows(Exception.class,
                () -> {
                    program.sample(startOfSampling, unsampledMeasurementsData);
                });
        assertEquals("Start of sample time not found" ,exception.getMessage());
    }

    @Test
     void sampleWithEmptyData() {
        unsampledMeasurementsData = new ArrayList<>();
        Exception exception = assertThrows(Exception.class,
                () -> {
                    program.sample(startOfSampling, unsampledMeasurementsData);
                });
        assertEquals("Measurements not found" ,exception.getMessage());
    }

    @Test
    void sampleDataIsNull() {
        unsampledMeasurementsData = null;
        Exception exception = assertThrows(Exception.class,
                () -> {
                    program.sample(startOfSampling, unsampledMeasurementsData);
                });
        assertEquals("Measurements not found" ,exception.getMessage());
    }

    @Test
    void testMeasurementConstructor(){
        LocalDateTime localDateTime = LocalDateTime.now();
        MeasurementType type = MeasurementType.HR;
        Double doubleValue = 35.01;
        Measurement measurement = new Measurement(localDateTime, type, doubleValue);
        assertEquals(measurement.getMeasurementTime(), localDateTime);
        assertEquals(measurement.getType(), type);
        assertEquals(measurement.getMeasurementValue(), doubleValue);
    }

    @Test
    void testMeasurementEquals(){
        LocalDateTime localDateTime = LocalDateTime.now();
        MeasurementType type = MeasurementType.HR;
        Double doubleValue = 35.01;
        Measurement firstMeasurement = new Measurement(localDateTime, type, doubleValue);

        Measurement secondMeasurement = new Measurement(localDateTime, type, doubleValue);
        secondMeasurement.setMeasurementTime(LocalDateTime.now().minusSeconds(1));

        Measurement thirdMeasurement = new Measurement(localDateTime, type, doubleValue);

        assertEquals(firstMeasurement, thirdMeasurement);
        assertNotEquals(firstMeasurement, secondMeasurement);

        Measurement fourthMeasurement = secondMeasurement;
        fourthMeasurement.setType(MeasurementType.SPO2);

        assertEquals(secondMeasurement, fourthMeasurement);

        Measurement fifthMeasurement = new Measurement(fourthMeasurement.getMeasurementTime(),
                fourthMeasurement.getType(),
                fourthMeasurement.getMeasurementValue());
        fourthMeasurement.setType(MeasurementType.HR);
        assertNotEquals(fourthMeasurement, fifthMeasurement);
    }

    @Test
    void testMeasurementToString(){
        LocalDateTime localDateTime = LocalDateTime.now();
        MeasurementType type = MeasurementType.HR;
        Double doubleValue = 35.01;
        Measurement firstMeasurement = new Measurement(localDateTime, type, doubleValue);
        String expectedString = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," +
                type + "," +
                doubleValue;

        assertEquals(firstMeasurement.toString(),  expectedString);

        firstMeasurement.setMeasurementTime(LocalDateTime.now());
        assertNotEquals(firstMeasurement.toString(),  expectedString);
    }

    //would test IF: would be using setters or had custom logic, validations is setters
    @Test
    void testMeasurementToStringIfNull(){
        LocalDateTime localDateTime = LocalDateTime.now();
        MeasurementType type = MeasurementType.HR;
        Double doubleValue = 35.01;
        Measurement firstMeasurement = new Measurement(localDateTime, type, doubleValue);

        firstMeasurement.setMeasurementTime(null);
        assertEquals(firstMeasurement.toString(), "");

        firstMeasurement.setMeasurementTime(localDateTime);
        firstMeasurement.setType(null);
        assertEquals(firstMeasurement.toString(), "");

        firstMeasurement.setType(MeasurementType.HR);
        firstMeasurement.setMeasurementValue(null);
        assertEquals(firstMeasurement.toString(), "");
    }

}