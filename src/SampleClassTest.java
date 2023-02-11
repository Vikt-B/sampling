import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
}