package pl.kolis.mobilevoter.utilities;

import java.util.UUID;

public class Constants {

    public static final String QUESTION = "Question";
    public static final String ANSWERS = "Answers";
    public static final String DURATION = "Duration";

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int DISCOVERABLE_REQ = 2;
    public static final int PICK_DEVICE = 3;
    public static final java.util.UUID MY_UUID =
            UUID.fromString("04E78CB0-1084-11E6-A837-0800200C9A66");
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int POLL_MSG = 1;
    public static final int VOTE_MSG = 2;
    public static final String IS_CLIENT = "show_rv";
    public static final String FIREBASE_POLL = "poll";
    public static final String POLL_ID = "poll_id";
    public static final String VOTES_ARRAY = "votes_array";

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String DEVICE = "device";

    public static final int REQUEST_ACCESS_COARSE_LOCATION = 22;
}
