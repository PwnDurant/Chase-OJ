import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;

@SpringBootTest
public class TestCode {
    public TestCode() {
    }

    public int[] twoSum(int[] var1, int var2) {
        HashMap var3 = new HashMap();

        for(int var4 = 0; var4 < var1.length; ++var4) {
            int var5 = var2 - var1[var4];
            if (var3.containsKey(var5)) {
                return new int[]{(Integer)var3.get(var5), var4};
            }

            var3.put(var1[var4], var4);
        }

        throw new IllegalArgumentException("No two sum solution");
    }

    public static void main(String[] var0) {
        int[] var1 = (new TestCode()).twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println(Arrays.toString(var1));
    }


}
