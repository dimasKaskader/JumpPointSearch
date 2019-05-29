import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        JumpPointSearch jps = new JumpPointSearch();

        Path path = Paths.get("file.txt");
        List<String> file = Files.readAllLines(path);
        char[][] map = new char[file.size()][file.get(0).length()];
        for(int i = 0; i < file.size(); i++)
        {
            String line = file.get(i);
            while(line.contains(" "))
                line = line.replace(' ', '.');
            map[i] = line.toCharArray();
        }

        char[][] res = jps.searchRoute(map);

        if(res != null)
        for(int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[0].length; j++)
                System.out.print(res[i][j]);
            System.out.println();
        }
    }
}
