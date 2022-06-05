package com.rugobal.mleap.scoring;

import com.rugobal.mleap.MLeapJavaUtils;
import ml.combust.mleap.core.types.StructField;
import ml.combust.mleap.core.types.StructType;
import ml.combust.mleap.runtime.frame.DefaultLeapFrame;
import ml.combust.mleap.runtime.frame.Row;
import ml.combust.mleap.runtime.frame.Transformer;
import ml.combust.mleap.runtime.javadsl.LeapFrameBuilder;
import ml.combust.mleap.runtime.javadsl.LeapFrameBuilderSupport;
import ml.combust.mleap.runtime.javadsl.LeapFrameSupport;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MLeapScorintTest {

    @Test
    public void testLocalScoring() {
        String modelPath = currentPath() + "/col-perc-mleap-test-spec.model.zip";
        LeapFrameBuilder leapFrameBuilder = new LeapFrameBuilder();
        LeapFrameBuilderSupport support = new LeapFrameBuilderSupport();
        Transformer model = MLeapJavaUtils.loadMleapModel(modelPath, null);
        List<StructField> structFieldList = Arrays.asList(
                leapFrameBuilder.createField("country", leapFrameBuilder.createString()));
        // Create the schema
        final StructType schema = leapFrameBuilder.createSchema(structFieldList);
        long start = System.currentTimeMillis();
        // Create rows
        final List<Row> rows = new ArrayList<>();
        rows.add(support.createRowFromIterable(Arrays.asList("SPAIN")));
        rows.add(support.createRowFromIterable(Arrays.asList("FRANCE")));
        rows.add(support.createRowFromIterable(Arrays.asList("ITALY")));
        rows.add(support.createRowFromIterable(Arrays.asList("UK")));
        rows.add(support.createRowFromIterable(Arrays.asList("HUNGARY")));
        DefaultLeapFrame frame = leapFrameBuilder.createFrame(schema, rows);
        DefaultLeapFrame transformed = model.transform(frame).get();
        long time = System.currentTimeMillis()-start;
        System.out.println(String.format("took %d millis to score", time));
        LeapFrameSupport leapSupport = new LeapFrameSupport();
        // convert(transformed.dataset()).forEach(System.out::println);
        leapSupport.collect(transformed).forEach(System.out::println);
        System.out.println();
        DefaultLeapFrame countryPercentages = leapSupport.select(transformed, Arrays.asList("country_perc"));
        leapSupport.collect(countryPercentages).forEach(System.out::println);

    }


    /**
     * Current path in linux style.
     *
     * If in a windows system it will remove the c: and substitute '\' by '/'
     *
     * @return
     */
    private String currentPath()  {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return Paths.get("").toAbsolutePath().toString().replace("C:", "").replace("\\", "/");
        } else {
            return Paths.get("").toAbsolutePath().toString();
        }
    }


}
