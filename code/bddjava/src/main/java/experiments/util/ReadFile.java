package experiments.util;

import bddgraph.Edge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadFile {

    public static Stream<Edge> read(String filePath, boolean oneIndexed) throws IOException {
        Stream<String> stream = Files.lines(Paths.get(filePath));
        int offset = oneIndexed ? -1 : 0;
        return stream
                .filter(line -> line.matches("[0-9]+[- \t][0-9]+.*"))
                .map(line -> line.split("[- \t]"))
                .map(lines -> new Edge(Integer.parseInt(lines[0]) + offset, Integer.parseInt(lines[1]) + offset));
    }

    public static int inferNodeAmount(String filePath, boolean oneIndexed) throws IOException {
        Stream<String> stream = Files.lines(Paths.get(filePath));
        int offset = oneIndexed ? 0 : 1;
        return offset + stream
                .filter(line -> line.matches("[0-9]+[- \t][0-9]+.*"))
                .map(line -> line.split("[- \t]"))
                .map(line -> Arrays.asList(line[0], line[1]))
                .flatMap(List::stream)
                .mapToInt(Integer::parseInt)
                .max().orElse(0);
    }

    public static boolean inferOneIndexed(String filePath) throws IOException {
        Stream<String> stream = Files.lines(Paths.get(filePath));
        return !stream
                .filter(line -> line.matches("[0-9]+[- \t][0-9]+.*"))
                .map(line -> line.split("[- \t]"))
                .map(line -> Arrays.asList(line[0], line[1]))
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .contains("0");
    }
}

