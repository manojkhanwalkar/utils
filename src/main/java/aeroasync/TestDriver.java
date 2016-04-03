package aeroasync;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkhanwalkar on 3/5/16.
 */
public class TestDriver {

    AeroWriter writer = new AeroWriter();

    public static void main(String[] args) throws Exception {
        TestDriver driver = new TestDriver();

        driver.drive();

        driver.writer.runQuery();

        Thread.sleep(10000);
    }

    public void drive()
    {
        List<Profile> profiles = new ArrayList<>();

        for (int i=0;i<3;i++) {

            Profile profile = createProfile();
            profiles.add(profile);
        }
        writer.bawrite(profiles);

    }




    private Profile createProfile()
    {
        Profile profile = new Profile();

        return profile ;

    }



}
