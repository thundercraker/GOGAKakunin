package com.yumashish.kakunin.External;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lightning on 1/12/16.
 */
public class SimpleOpeningAndClosingParser {
    /* <time-str> := [ `"` ] { <time-line> } [ `"` ]
     * <time-line> := ( <open> | <closed> )
     * <time-label> := <day> [ ( { <day> } | <day-sep> <day>) ]
     *
     * <open> := [ <time-label> ":" ] <time-value> <time-sep> <time-value>
     * <closed> := <closed-day> ":" (<none> | <day> { "・" <day> } )
     * <day> := <mon> | ... | <sunday> | <holiday>
     * <time-value> := <number> ":" <number>
     *
     * 日祝 sunday/holiday
     * 定休日： 不定休 holidays : none
     */
    List<String> mDaysOfTheWeek;
    String mHoliday, mStoreClosed, mNone, mLabelSep, mHourMinSep, mTimeSep, mDaySep, mLastOrder, mMerchStore;
    LinkedList<Character> mQueue;

    //0-6 Monday to Sunday
    //7 - Holidays
    OpeningAndClosingTime[] mResolvedTimes;
    List<Integer> mClosedDays;

    public SimpleOpeningAndClosingParser(List<String> daysOfTheWeekStartingMonday, String holiday, String storeClosed, String lastOrder, String merchStore, String none,
                                         String labelSep, String hourMinSep, String timeSep, String daySep) {
        mDaysOfTheWeek = daysOfTheWeekStartingMonday;
        mHoliday = holiday;
        mStoreClosed = storeClosed;
        mLabelSep = labelSep;
        mTimeSep = timeSep;
        mDaySep = daySep;
        mNone = none;
        mHourMinSep = hourMinSep;
        mLastOrder = lastOrder;
        mMerchStore = merchStore;

        mResolvedTimes = new OpeningAndClosingTime[8];
        mClosedDays = new ArrayList<Integer>();
        mQueue = new LinkedList<Character>();
    }

    public SimpleOpeningAndClosing getResult() {
        int[] wtfIsWrongWithJava = new int[mClosedDays.size()];
        int i = 0;
        for (Integer val : mClosedDays) {
            wtfIsWrongWithJava[i++] = val;
        }
        return new SimpleOpeningAndClosing(mResolvedTimes, wtfIsWrongWithJava);
    }

    public static class SimpleOpeningAndClosing {
        final OpeningAndClosingTime[] openingAndClosingTimes;
        final int[] closedDays;

        public SimpleOpeningAndClosing(OpeningAndClosingTime[] resolvedOpeningAndClosing, int[] resolvedClosedDays) {
            openingAndClosingTimes = resolvedOpeningAndClosing;
            closedDays = resolvedClosedDays;
        }

        private static boolean IsBefore(HoursMinsTime time, int hours, int mins) {
            if(time.Hours > hours) return true;
            else if(time.Hours == hours && time.Mins > mins) return true;
            else return false;
        }

        private static boolean IsAfter(HoursMinsTime time, int hours, int mins) {
            if(time.Hours < hours) return true;
            else if(time.Hours == hours && time.Mins < mins) return true;
            else return false;
        }

        public boolean IsOpenAt(int day, int hours, int mins) {
            if(openingAndClosingTimes[day] != null && IsAfter(openingAndClosingTimes[day].Opening, hours, mins)
                    && IsBefore(openingAndClosingTimes[day].Closing, hours, mins)) {
                return true;
            }
            return false;
        }
    }

    public void Dump() {
        for (OpeningAndClosingTime res : mResolvedTimes) {
            //System.out.println(res);
        }
    }

    public static SimpleOpeningAndClosingParser ExampleParser() {
        String[] days = new String[] { "月", "火", "水", "木", "金", "土", "日", "祝" };
        return new SimpleOpeningAndClosingParser(Arrays.asList(days), "祝", "定休日", "ラストオーダー", "資材館", "不定休", "：", ":", "～", "・");
    }

    public class OpeningTimeParseException extends Exception {
        public OpeningTimeParseException(String msg) {
            super(msg);
        }
    }

    public static class OpeningAndClosingTime {
        public HoursMinsTime Opening;
        public HoursMinsTime Closing;
        public OpeningAndClosingTime(HoursMinsTime Opening, HoursMinsTime Closing) {
            this.Opening = Opening;
            this.Closing = Closing;
        }
        public String toString() {
            return "Hours: " + Opening.toString() + " Closing: " + Closing.toString();
        }
    }

    public static class HoursMinsTime {
        public int Hours;
        public int Mins;
        public HoursMinsTime(int Hours, int Mins) {
            this.Hours = Hours;
            this.Mins = Mins;
        }

        public String toString() {
            return Hours + ":" + Mins;
        }
    }

    public boolean Parse(String raw) {
        consumed = new StringBuilder();
        boolean success = true;
        if(raw.charAt(0) == '"') {
            raw = raw.substring(1, raw.length() - 1);
        }
        raw = raw.trim();
        for (char c : raw.toCharArray()) {
            mQueue.add(c);
        }

        while (!mQueue.isEmpty()) {
            try {
                TimeLine();
            } catch (OpeningTimeParseException e) {
                DumpLatest();
                e.printStackTrace();
                success = false;
                break;
            } catch (NullPointerException e) {
                DumpLatest();
                e.printStackTrace();
                success = false;
                break;
            } catch (Exception e) {
                DumpLatest();
                e.printStackTrace();
                success = false;
                break;
            }
        }
        return success;
    }

    StringBuilder consumed;

    void DumpLatest() {
        //System.out.println("Expected Token -> [" + Consuming + "]");
        //System.out.println("Consumed Queue -> [" + consumed.toString() + "]");
        String s = "Current Queue -> [";
        for (int i = 0; i < 10 && !mQueue.isEmpty(); i++) {
            s += mQueue.removeFirst();
        }
        s+="]";
        //System.out.println(s);
    }

    void Expect(boolean e) throws OpeningTimeParseException {
        if (!e) {
            //error
            //System.out.println("Unexpected " + mQueue.peek());
            throw new OpeningTimeParseException("Parse exception");
        }
    }

    private String Consuming = "";
    boolean Consume(String token) {
        Consuming = token;
        int consume = 0;
        if(mQueue.isEmpty()) return false;
        //ignore leading whitespaces
        if(Character.isWhitespace(mQueue.peek())) mQueue.removeFirst();
        List<Character> removed = new ArrayList<Character>();
        for(; consume < token.length(); consume++) {
            if(mQueue.peek() != token.toCharArray()[consume]) {
                ////System.out.println("Rejected: " + token + " " + mQueue.peek() + " != " + token.toCharArray()[consume]);
                for (int i = 0; i < consume; i++) {
                    mQueue.addFirst(removed.get(i));
                }
                return false;
            }
            removed.add(mQueue.removeFirst());
        }
        for (char a : removed) {
            consumed.append(a);
        }
        ////System.out.println("Accepted: " + token);
        return true;
    }

    public int Number() throws OpeningTimeParseException {
        //ignore leading whitespaces
        if(Character.isWhitespace(mQueue.peek())) mQueue.removeFirst();
        String num = "";
        while(!mQueue.isEmpty() && Character.isDigit(mQueue.peek())) {
            num += mQueue.removeFirst();
        }
        consumed.append(num);
        if (num.length() < 1) throw new OpeningTimeParseException("Expecting number got non-number");
        ////System.out.println("Accepted: " + num);
        return Integer.parseInt(num);
    }

    public void TimeLine() throws OpeningTimeParseException {
        if(Consume(mStoreClosed)) {
            Expect(Consume(mLabelSep));
            int day;
            if ((day = Day()) != -1) {
                List<Integer> days = new ArrayList<Integer>();
                days.add(day);
                while (Consume(mDaySep)) {
                    Expect((day = Day()) != -1);
                    days.add(day);
                }
                mClosedDays = days;
            } else {
                Expect(Consume(mNone));
            }
        } else if (Consume(mLastOrder)) {
            Expect(Consume(mLabelSep));
            TimeValue();
        } else if (Consume(mMerchStore)) {
            Consume(mLabelSep);
            TimeValue();
            Expect(Consume(mTimeSep));
            TimeValue();
        } else {
            ReadDayTime();
        }
    }

    public void ReadDayTime() throws OpeningTimeParseException {
        List<Integer> days;
        if((days = TimeLabel(false)) != null) {
            Expect(Consume(mLabelSep));
        }
        HoursMinsTime opening = TimeValue();
        Expect(Consume(mTimeSep));
        HoursMinsTime closing = TimeValue();
        if(days != null) {
            for(int i : days) {
                mResolvedTimes[i] = new OpeningAndClosingTime(opening, closing);
            }
        } else {
            for(int i = 0; i < 8; i++) {
                mResolvedTimes[i] = new OpeningAndClosingTime(opening, closing);
            }
        }
    }

    public List<Integer> TimeLabel(boolean expected) throws OpeningTimeParseException {
        List<Integer> days = new ArrayList<Integer>();
        days.add(Day());
        boolean accept = days.get(0) != -1;
        if (expected) Expect(accept);
        else if(!accept) return null;
        int day;
        if((day = Day()) != -1) {
            days.add(day);
            while((day = Day()) != -1) {
                days.add(day);
            }
        } else if(Consume(mTimeSep)) {
            Expect((day = Day()) != -1);
            for (int i = days.get(0); i <= day; i++) {
                days.add(i);
            }
        }
        return days;
    }

    public HoursMinsTime TimeValue() throws OpeningTimeParseException {
        int hours = Number();
        Expect(Consume(mHourMinSep));
        int mins = Number();
        return new HoursMinsTime(hours, mins);
    }



    public Integer Day() {
        int day = -1;
        for (int i = 0; i < 8; i++) {
            if(Consume(mDaysOfTheWeek.get(i))) {
                day = i;
                break;
            }
        }
        return day;
    }

    public boolean DaySep() {
        return Consume(mLabelSep);
    }

}
