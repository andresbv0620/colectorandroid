package co.colector.settings;

/**
 * Created by dherrera on 11/10/15.
 */
public class AppSettings {

    // ------------------ TABS --------------------
    public static String TAB_ID_AVAILABLE_SURVEY = "tab_id_available_tabs";
    public static String TAB_ID_DONE_SURVEY = "tab_id_done_tabs";
    public static String TAB_ID_UPLOADED_SURVEY =  "tab_id_uploaded_tabs";


    // ------------------ DATABASE ------------------
    public static String DB_NAME = "v2016_colector";
    public static int DB_VERSION = 7;

    // ------------------ HTTP CODES ---------------
    public static Long HTTP_OK = 200L;

    // ------------------ LOG ------------------
    public static String TAG = "Colector";

    //-----------------Flags Service----------
    public static int SERVICE_FLAG_DELETE = 2;
    public static int SERVICE_FLAG_UPLOAD = 1;
    public static int SERVICE_FLAG_SYNCFORM = 0;

    //-----------Flags TypeSurveySelected------
    public static int SURVEY_SELECTED_NEW = 0;
    public static int SURVEY_SELECTED_EDIT = 1;

    public static String URL_BASE;
}
