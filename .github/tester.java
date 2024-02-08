///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//DEPS com.fasterxml.jackson.core:jackson-databind:2.13.0
import static java.lang.System.*;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class tester {

    public static void main(String... args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        File f = new File("jbang-catalog.json");
        if(args.length > 0) {
            f = new File(args[0]);
        }
        var tree = mapper.readTree(f);
        int[] exit = new int[1];
        tree.get("aliases").forEach(
            alias -> {
                String scriptRef = alias.get("script-ref").asText();
                ProcessBuilder pb = new ProcessBuilder("jbang", "build", scriptRef);
                try {
                    var process = pb.inheritIO().start();
                    var ex = process.waitFor();
                    if(ex != 0) {
                        exit[0]++;
                        out.println("FAIL: %s".formatted(scriptRef));
                    } else {
                        out.println("PASS: %s".formatted(scriptRef));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exit[0]++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    exit[0]++;
                }
            }
        );
        //System.out.println("Exit code: " + exit[0]);
        System.exit(exit[0]);
    }
}
