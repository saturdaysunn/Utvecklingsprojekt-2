package all.jointEntity;

import java.io.Serializable;

    public class TimeMessage implements Serializable {
        private String time1;
        private String time2;

        public TimeMessage(String time1, String time2) {
            this.time1 = time1;
            this.time2 = time2;
        }

        public String getTime1() {
            return time1;
        }

        public String getTime2() {
            return time2;
        }

        public void setTime1(String time1) {
            this.time1 = time1;
        }

        public void setTime2(String time2) {
            this.time2 = time2;
        }

    }
