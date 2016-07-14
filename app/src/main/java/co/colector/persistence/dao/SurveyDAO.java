package co.colector.persistence.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import co.colector.model.IdOptionValue;
import co.colector.model.IdValue;
import co.colector.model.Question;
import co.colector.model.ResponseAttribute;
import co.colector.model.ResponseComplex;
import co.colector.model.ResponseItem;
import co.colector.model.Section;
import co.colector.model.Survey;
import co.colector.model.SurveySave;
import co.colector.persistence.DriverSQL;
import co.colector.settings.AppSettings;

public class SurveyDAO extends DriverSQL {

	private final static String TBL_NAME = "TBL_SURVEY";
	private final static String TBL_NAME_SURVEY_INSTANCE = "TBL_SURVEY_INSTANCE";
	private final static String TBL_NAME_SURVEY_INSTANCE_DETAIL = "TBL_SURVEY_INSTANCE_DETAIL";
	private final static String TBL_NAME_SECTION = "TBL_SECTION";
	private final static String TBL_NAME_QUESTION = "TBL_QUESTION";
	private final static String TBL_NAME_RESPONSE_COMPLEX = "TBL_RESPONSE_COMPLEX";
	private final static String TBL_NAME_RESPONSE_COMPLEX_OPTION = "TBL_RESPONSE_COMPLEX_OPTION";
    private final static String TBL_NAME_RESPONSE_ATTRIBUTES = "TBL_RESPONSE_ATTRIBUTES";
	private final static String TBL_NAME_RESPONSE = "TBL_RESPONSE";

	private SQLiteDatabase db;

	public SurveyDAO(Context ctx) {
		super(ctx);
	}


    // ----------------------------- QUERY -----------------------------------

    /**
     * Return list of survey available
     * @return
     */
    public List<Survey> getSurveyDone(String Status){

        db = getDBRead();
        List<Survey> toReturn = new ArrayList<Survey>();
        String[] fields = new String[] { "ID_SURVEY","DATE_INSTANCE","ID", "LATITUDE", "LONGITUDE", "HORAINI", "HORAFIN" };
        String[] fieldsSurvey = new String[] { "ID", "NAME", "DESCRIPTION", "PRECARGADO" };
        String[] where = new String[] { Status };

        Cursor cursor = db.query(TBL_NAME_SURVEY_INSTANCE, fields, "STATUS=?", where, null, null, null);
        try {


            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Long surveyId = cursor.getLong(0);
                        String instanceDate = cursor.getString(1);
                        Long instanceId = cursor.getLong(2);
                        String instanceLogitude = cursor.getString(3);
                        String instanceAltitude= cursor.getString(4);
                        String instanceHoraIni = cursor.getString(5);
                        String instanceHoraFin = cursor.getString(6);
                        Cursor cursorSurvey = db.query(TBL_NAME, fieldsSurvey, "ID=?", new String[]{String.valueOf(surveyId)}, null, null, null);

                        if (cursorSurvey.getCount() > 0) {
                            if (cursorSurvey.moveToFirst()) {
                                do {
                                    Survey survey = new Survey();
                                    survey.setForm_id(cursorSurvey.getLong(0));
                                    survey.setForm_names(cursorSurvey.getString(1));
                                    survey.setForm_description(cursorSurvey.getString(2));
                                    survey.setForm_precargados(getBoolean(cursor.getString(3)));
                                    survey.setInstanceId(instanceId);
                                    survey.setInstanceDate(instanceDate);
                                    survey.setInstanceLongitude(instanceLogitude);
                                    survey.setInstanceLatitude(instanceAltitude);
                                    survey.setInstanceHoraIni(instanceHoraIni);
                                    survey.setInstanceHoraFin(instanceHoraFin);
                                    getSurveySections(db, survey);
                                    getSurveyInstanceDetail(db, instanceId, survey);

                                    toReturn.add(survey);

                                } while (cursorSurvey.moveToNext());
                            }
                        }

                    } while (cursor.moveToNext());

                }
            }

        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME + ".";
            Log.e(AppSettings.TAG, msg, se);
        }catch (Exception e) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME + ".";
            Log.e(AppSettings.TAG, msg, e);
        } finally {
            if(cursor != null)
                cursor.close();
            close();
        }

        return toReturn;
    }

    public List<Survey> getSurveyDonePrecar(String nameSurveyPrecar){

        db = getDBRead();
        List<Survey> toReturn = new ArrayList<Survey>();

        String[] fields = new String[] { "ID_SURVEY","DATE_INSTANCE","ID", "LATITUDE", "LONGITUDE", "HORAINI", "HORAFIN" };
        String[] fieldsSurvey = new String[] { "ID", "NAME", "DESCRIPTION", "PRECARGADO" };
        String[] where = new String[] { "RECURSIVO" };

        Cursor cursor = db.query(TBL_NAME_SURVEY_INSTANCE, fields, "STATUS=?", where, null, null, null);
        Survey survey = new Survey();
        try {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Long surveyId = cursor.getLong(0);
                        String instanceDate = cursor.getString(1);
                        Long instanceId = cursor.getLong(2);
                        String instanceLogitude = cursor.getString(3);
                        String instanceAltitude= cursor.getString(4);
                        String instanceHoraIni = cursor.getString(5);
                        String instanceHoraFin = cursor.getString(6);

                        Cursor cursorSurvey = db.query(TBL_NAME, fieldsSurvey, "ID=? AND NAME=?", new String[]{String.valueOf(surveyId), nameSurveyPrecar}, null, null, null);

                        if (cursorSurvey.getCount() > 0) {
                            if (cursorSurvey.moveToFirst()) {
                                do {
                                    survey = new Survey();
                                    survey.setForm_id(cursorSurvey.getLong(0));
                                    survey.setForm_names(cursorSurvey.getString(1));
                                    survey.setForm_description(cursorSurvey.getString(2));
                                    survey.setForm_precargados(getBoolean(cursor.getString(3)));

                                    survey.setInstanceId(instanceId);
                                    survey.setInstanceDate(instanceDate);
                                    survey.setInstanceLongitude(instanceLogitude);
                                    survey.setInstanceLatitude(instanceAltitude);
                                    survey.setInstanceHoraIni(instanceHoraIni);
                                    survey.setInstanceHoraFin(instanceHoraFin);
                                    getSurveySections(db, survey);
                                    getSurveyInstanceDetail(db, instanceId, survey);


                                } while (cursorSurvey.moveToNext());
                            }
                        }

                    } while (cursor.moveToNext());

                }
            }

            if (survey!=null)
                toReturn.add(survey);

        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME + ".";
            Log.e(AppSettings.TAG, msg, se);

        } finally {
            if(cursor != null)
                cursor.close();

            close();
        }

        return toReturn;
    }

    /**
     * Save instance details in survey
     * @param db
     * @param instances
     * @param survey
     */
    private void getSurveyInstanceDetail(SQLiteDatabase db, Long instances,Survey survey){

        String[] fields = new String[] { "ID_QUESTION","ANSWER" };
        String[] where = new String[] { String.valueOf(instances) };

        try{
            Cursor cursor = db.query(TBL_NAME_SURVEY_INSTANCE_DETAIL, fields, "ID_INSTANCE=?", where, null, null, null);

            Log.w(AppSettings.TAG, TBL_NAME_SURVEY_INSTANCE_DETAIL + " >>>>>> " + instances);
            if (cursor.getCount() > 0) {
                if (cursor.moveToLast()) {
                    do {
                        survey.getInstanceAnswers().add(new IdValue(cursor.getLong(0), cursor.getString(1),null));
                    } while (cursor.moveToPrevious());
                }
            }
            if(cursor != null)
                cursor.close();
        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME_SURVEY_INSTANCE_DETAIL + ".";
            Log.e(AppSettings.TAG, msg, se);
        }
    }

    /**
     * Return list of survey available
     * @return
     */
    public List<Survey> getSurveyAvailable(){

        db = getDBRead();
        List<Survey> toReturn = new ArrayList<Survey>();
        String[] fields = new String[] { "ID", "NAME", "DESCRIPTION","PRECARGADO",};

        Cursor cursor = db.query(TBL_NAME, fields, null, null, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Survey survey = new Survey();
                        survey.setForm_id(cursor.getLong(0));
                        survey.setForm_names(cursor.getString(1));
                        survey.setForm_description(cursor.getString(2));
                        survey.setForm_precargados(getBoolean(cursor.getString(3)));
                        getSurveySections(db, survey);

                        toReturn.add(survey);

                    } while (cursor.moveToNext());
                }
            }

        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME + ".";
            Log.e(AppSettings.TAG, msg, se);

        } finally {
            if(cursor != null)
                cursor.close();

            close();
        }

        return toReturn;
    }



    /**
     * Get and insert section from survey
     * @param db
     * @param survey
     */
    private void getSurveySections(SQLiteDatabase db, Survey survey){

        String[] fields = new String[] { "ID", "NAME", "DESCRIPTION" };
        String[] where = new String[] { String.valueOf(survey.getForm_id()) };

        try {
            Cursor cursor = db.query(TBL_NAME_SECTION, fields, "SURVEY=?",where, null, null, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Section section = new Section();
                        section.setId(cursor.getLong(0));
                        section.setName(cursor.getString(1));
                        section.setDescription(cursor.getString(2));
                        getSurveyQuestions(db, section);

                        survey.getSections().add(section);
                    } while (cursor.moveToNext());
                }
            }

            if(cursor != null)
                cursor.close();

        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME_SECTION + ".";
            Log.e(AppSettings.TAG, msg, se);

        }
    }

    /**
     * Get and insert question from section
     * @param db
     * @param section
     */
    private void getSurveyQuestions(SQLiteDatabase db, Section section){
        String[] fields = new String[] { "ID","NAME","DESCRIPTION","TYPE","MIN","MAX","DEFECTO","REQUIRED","VALIDACION","DEFECTO_PREVIO","SOLO_LECTURA","OCULTO","ORDEN" };
        String[] where = new String[] { String.valueOf(section.getId()) };

        Cursor cursor = db.query(TBL_NAME_QUESTION, fields, "SECTION=?", where, null, null, null);
        try {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Question question = new Question();
                        question.setId(cursor.getLong(0));
                        question.setName(cursor.getString(1));
                        question.setDescription(cursor.getString(2));
                        question.setType(cursor.getInt(3));
                        question.setMin(cursor.getString(4));
                        question.setMax(cursor.getString(5));
                        //question.setDefecto(cursor.getString(6));
                        question.setRequerido(getBoolean(cursor.getString(7)));
                        question.setValidacion(cursor.getString(8));
                        question.setDefectoPrevio(getBoolean(cursor.getString(9)));
                        question.setSoloLectura(cursor.getString(10));
                        String ocultos= cursor.getString(11);
                        question.setOculto(getBoolean(ocultos));
                        question.setOrden(cursor.getString(12));
                        getSurveyQuestionResponse(db, question);
                        getSurveyQuestionComplex(db, question);
                        getSurveyQuestionAttributes(db, question);

                        section.getInputs().add(question);
                    } while (cursor.moveToNext());
                }
            }

        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME_QUESTION + ".";
            Log.e(AppSettings.TAG, msg, se);

        }catch (Exception e){
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME_QUESTION + ".";
            Log.e(AppSettings.TAG, msg, e);
        }finally {
            if(cursor != null)
                cursor.close();
        }


    }

    public boolean getBoolean(String column) {
        if (column.equalsIgnoreCase("false")) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * Get and insert response from Question
     * @param db
     * @param question
     */
    private void getSurveyQuestionResponse(SQLiteDatabase db, Question question){


        String[] fields = new String[] { "ID","VALUE"};
        String[] where = new String[] { String.valueOf(question.getId()) };

        Cursor cursor = db.query(TBL_NAME_RESPONSE, fields, "QUESTION=?", where, null, null, null);


        try {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        IdOptionValue response = new IdOptionValue();
                        response.setId(cursor.getLong(0));
                        response.setValue(cursor.getString(1));

                        question.getResponses().add(response);
                    } while (cursor.moveToNext());
                }
            }

        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME_RESPONSE+ ".";
            Log.e(AppSettings.TAG, msg, se);

        }
    }

    /**
     * Get and insert response from Question
     * @param db
     * @param question
     */
    private void getSurveyQuestionComplex(SQLiteDatabase db, Question question){

        String[] fields = new String[] { "ID"};
        String[] where = new String[] { String.valueOf(question.getId()) };

        Cursor cursor = db.query(TBL_NAME_RESPONSE_COMPLEX, fields, "QUESTION=?", where, null, null, null);


        try {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        ResponseComplex response = new ResponseComplex();
                        response.setRecord_id(response.getRecord_id());

                        getSurveyQuestionComplexOptions(db, response);

                        question.getOptions().add(response);
                    } while (cursor.moveToNext());
                }
            }

        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME_RESPONSE_COMPLEX + ".";
            Log.e(AppSettings.TAG, msg, se);

        }
        if(cursor != null)
            cursor.close();

    }



    /**
     * Get and insert response from Question
     * @param db
     * @param complex
     */
    private void getSurveyQuestionComplexOptions(SQLiteDatabase db, ResponseComplex complex){

        String[] fields = new String[] { "ID, LABEL, VALUE, TYPE"};
        String[] where = new String[] { String.valueOf(complex.getRecord_id()) };

        Cursor cursor = db.query(TBL_NAME_RESPONSE_COMPLEX_OPTION, fields, "COMPLEX=?", where, null, null, null);


        try {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        ResponseItem response = new ResponseItem();
                        response.setInput_id(cursor.getLong(0));
                        response.setLabel(cursor.getString(1));
                        response.setValue(cursor.getString(2));
                        response.setTipo(cursor.getLong(3));

                        complex.getResponses().add(response);
                    } while (cursor.moveToNext());
                }
            }

        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME_RESPONSE_COMPLEX_OPTION + ".";
            Log.e(AppSettings.TAG, msg, se);

        }
        if(cursor != null)
            cursor.close();

    }

    /**
     * Get and insert response from Question
     * @param db
     * @param question
     */
    private void getSurveyQuestionAttributes(SQLiteDatabase db, Question question){

        String[] fields = new String[] { "ID, LABEL, TYPE"};
        String[] where = new String[] { String.valueOf(question.getId()) };

        Cursor cursor = db.query(TBL_NAME_RESPONSE_ATTRIBUTES, fields, "QUESTION=?", where, null, null, null);


        try {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        ResponseAttribute response = new ResponseAttribute();
                        response.setInput_id(cursor.getLong(0));
                        response.setLabel(cursor.getString(1));
                        response.setType(cursor.getInt(2));

                        question.getAtributos().add(response);
                    } while (cursor.moveToNext());
                }
            }

        }catch (SQLException se) {
            String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME_RESPONSE_ATTRIBUTES + ".";
            Log.e(AppSettings.TAG, msg, se);

        }
        if(cursor != null)
            cursor.close();

    }


    // ----------------------------- INSERT -----------------------------------

	/**
	 * Insert or update surveys from login process
	 * 
	 * @param surveys list of surveys to insert
	 */
	public void synchronizeSurveys(List<Survey> surveys) {
		SQLiteDatabase db = getDBWrite();

        try{
		for (Survey survey:surveys){
			ContentValues initialValues = new ContentValues();
			initialValues.put("ID", survey.getForm_id());
			initialValues.put("NAME", survey.getForm_name());
			initialValues.put("DESCRIPTION", survey.getForm_description());
            initialValues.put("PRECARGADO", "" + survey.getForm_precargados());

			// Inserta o actualiza un registro
			if ((int) db.insertWithOnConflict(TBL_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
				db.update(TBL_NAME, initialValues, "ID=?", new String[]{String.valueOf(survey.getForm_id())});
			}

            synchronizeSections(survey.getSections(), survey.getForm_id(), db);

		}}
            catch (SQLException se) {
                String msg = "Ha ocurrido un error recuperando los datos de la tabla " + TBL_NAME + ".";
                Log.e(AppSettings.TAG, msg, se);

            } finally {
                close();
            }
	}

	/**
	 * Insert or update sections from login process
	 * @param sections list of sections to insert
	 * @param db db conection
	 */
	private void synchronizeSections(List<Section> sections, Long survey,SQLiteDatabase db){
		for (Section section: sections) {
			ContentValues initialValues = new ContentValues();
			initialValues.put("ID", section.getId());
			initialValues.put("NAME", section.getName());
			initialValues.put("DESCRIPTION", section.getDescription());
			initialValues.put("SURVEY", survey);

			// Inserta o actualiza un registro
			if ((int) db.insertWithOnConflict(TBL_NAME_SECTION, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
				db.update(TBL_NAME_SECTION, initialValues, "ID=?", new String[]{String.valueOf(section.getId())});
			}
            synchronizeQuestion(section.getInputs(), section.getId(), db);
		}
	}

    /**
     * Insert or update questions from login process
     * @param questions list of questions to insert
     * @param db db conection
     */
    private void synchronizeQuestion(List<Question> questions, Long section,SQLiteDatabase db){
        for (Question question: questions) {

            ContentValues initialValues = new ContentValues();
            initialValues.put("ID", question.getId());
            initialValues.put("TYPE", question.getType());

            initialValues.put("NAME", question.getName());
            initialValues.put("DESCRIPTION", question.getDescription());
            initialValues.put("MIN", question.getMin());
            initialValues.put("MAX", question.getMax());
            initialValues.put("DEFECTO", question.getDefecto());
            initialValues.put("REQUIRED", "" + question.getRequerido());
            initialValues.put("VALIDACION", question.getValidacion());
            initialValues.put("DEFECTO_PREVIO", "" + question.getDefectoPrevio());
            initialValues.put("SOLO_LECTURA", question.getSoloLectura());
            initialValues.put("OCULTO", "" + question.getoculto());
            initialValues.put("ORDEN", question.getOrden());
            initialValues.put("SECTION", section);

            // Inserta o actualiza un registro
            if ((int) db.insertWithOnConflict(TBL_NAME_QUESTION, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
                db.update(TBL_NAME_QUESTION, initialValues, "ID=?", new String[]{String.valueOf(question.getId())});
            }

            synchronizeResponses(question.getResponses(), question.getId(), db);
            synchronizeResponsesComplex(question.getOptions(), question.getId(), db);
            synchronizeResponsesAttributes(question.getAtributos(), question.getId(), db);
        }
    }

    /**
     * Insert or update responses from login process
     * @param responses list of responses to insert
     * @param db db conection
     */
    private void synchronizeResponses(List<IdOptionValue> responses, Long question,SQLiteDatabase db){
        for (IdOptionValue response: responses) {

            ContentValues initialValues = new ContentValues();
            initialValues.put("ID", response.getId());
            initialValues.put("VALUE", response.getValue());
            initialValues.put("QUESTION", question);

            // Inserta o actualiza un registro
            if ((int) db.insertWithOnConflict(TBL_NAME_RESPONSE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
                db.update(TBL_NAME_RESPONSE, initialValues, "ID=?", new String[]{String.valueOf(response.getId())});
            }
        }
    }

    /**
     * Insert or update responses from login process
     * @param responses list of responses to insert
     * @param db db conection
     */
    private void synchronizeResponsesComplex(List<ResponseComplex> responses, Long question,SQLiteDatabase db){

        if(responses != null) {

            for (ResponseComplex response : responses) {

                ContentValues initialValues = new ContentValues();
                initialValues.put("ID", response.getRecord_id());
                initialValues.put("QUESTION", question);

                // Inserta o actualiza un registro
                if ((int) db.insertWithOnConflict(TBL_NAME_RESPONSE_COMPLEX, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
                    db.update(TBL_NAME_RESPONSE_COMPLEX, initialValues, "ID=?", new String[]{String.valueOf(response.getRecord_id())});
                }
                synchronizeResponsesComplexOptions(response.getResponses(), response.getRecord_id(), db);
            }
        }
    }

    /**
     * Insert or update responses from login process
     * @param responses list of responses to insert
     * @param db db conection
     */
    private void synchronizeResponsesComplexOptions(List<ResponseItem> responses, String complex,SQLiteDatabase db){
        for (ResponseItem response: responses) {

            ContentValues initialValues = new ContentValues();
            initialValues.put("ID", response.getInput_id());
            initialValues.put("LABEL", response.getLabel());
            initialValues.put("TYPE", response.getTipo());
            initialValues.put("VALUE", response.getValue());
            initialValues.put("COMPLEX", complex);

            // Inserta o actualiza un registro
            if ((int) db.insertWithOnConflict(TBL_NAME_RESPONSE_COMPLEX_OPTION, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
                db.update(TBL_NAME_RESPONSE_COMPLEX_OPTION, initialValues, "ID=? AND COMPLEX=?", new String[]{String.valueOf(response.getInput_id()),complex});
            }
        }
    }

    /**
     * Modify survey instance
     * @param survey
     */
    public Long modifySurveyInstance(SurveySave survey){

        SQLiteDatabase db = getDBWrite();
        try{

            ContentValues initialValues = new ContentValues();
            initialValues.put("STATUS", "FALSE");

            db.update(TBL_NAME_SURVEY_INSTANCE, initialValues, "ID=?",
                    new String[]{String.valueOf(survey.getInstanceId())});

        for (IdValue toInsert : survey.getResponses()){
            modifySurveyInstanceDetail(db,survey.getInstanceId(),toInsert.getId(),toInsert.getValue());
        }}catch (SQLException se) {
            String msg = "Ha ocurrido un error actualizando una encuesta.";
            Log.e(AppSettings.TAG, msg, se);
            return -1L;
        } finally {
            close();
        }
        return survey.getInstanceId();
    }

    /**
     * Save survey instance
     * @param survey
     */
    public Long saveSurveyInstance(SurveySave survey){

        SQLiteDatabase db = getDBWrite();

        ContentValues initialValues = new ContentValues();
        initialValues.put("ID_SURVEY", survey.getId());
        initialValues.put("DATE_INSTANCE", String.valueOf(new Timestamp(new java.util.Date().getTime())));
        initialValues.put("LATITUDE", survey.getLatitude());
        initialValues.put("LONGITUDE", survey.getLongitude());
        initialValues.put("HORAINI", survey.getHoraIni());
        initialValues.put("HORAFIN", survey.getHoraFin());

        Long toReturn = db.insert(TBL_NAME_SURVEY_INSTANCE, null, initialValues);

        if(toReturn!=-1) {

            for (IdValue toInsert : survey.getResponses()){
                saveSurveyInstanceDetail(db,toReturn,toInsert.getId(),toInsert.getValue());
            }
        }
        close();
        return toReturn;
    }

    /**
     * Save survey details
     * @param db
     * @param instances
     * @param question
     * @param answer
     */
    private void saveSurveyInstanceDetail(SQLiteDatabase db, Long instances,Long question, String answer){

        ContentValues initialValues = new ContentValues();
        initialValues.put("ID_INSTANCE", instances);
        initialValues.put("ID_QUESTION", question);
        initialValues.put("ANSWER", answer);

        // Inserta o actualiza un registro
        if ((int) db.insertWithOnConflict(TBL_NAME_SURVEY_INSTANCE_DETAIL, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
            db.update(TBL_NAME_SURVEY_INSTANCE_DETAIL, initialValues, "ID_INSTANCE=? AND ID_QUESTION=?", new String[]{String.valueOf(instances),String.valueOf(question)});
        }
    }

    /**
     * Save survey details
     * @param db
     * @param instances
     * @param question
     * @param answer
     */
    private void modifySurveyInstanceDetail(SQLiteDatabase db, Long instances,Long question, String answer){

        ContentValues initialValues = new ContentValues();
        initialValues.put("ID_INSTANCE", instances);
        initialValues.put("ID_QUESTION", question);
        initialValues.put("ANSWER", answer);

        db.update(TBL_NAME_SURVEY_INSTANCE_DETAIL, initialValues, "ID_INSTANCE=? AND ID_QUESTION=?",
                new String[]{String.valueOf(instances), String.valueOf(question)});

    }



    /**
     * Delete a survey instance
     * @param instances
     */
    public int deleteSurveysInstance(Long instances){
        SQLiteDatabase db = getDBWrite();
        String[] where = new String[] { String.valueOf(instances) };

        int toReturn = db.delete(TBL_NAME_SURVEY_INSTANCE, "ID=?", where);

        if(toReturn==1) {
            deleteSurveyInstanceDetail(db,instances);
        }
        close();
        return toReturn;
    }

    public int statusSurveyInstance(Long instances, Boolean Precargado){
        SQLiteDatabase db = getDBWrite();
        String Estado;
        if (Precargado){
            Estado="RECURSIVO";  //RECURSIVO ES IGUAL A ENVIADO // PERO NO SE ELIMINAN, PARA PODER LEERLO..
        }else{
            Estado="ENVIADO";
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put("STATUS", Estado);
        String[] where = new String[] { String.valueOf(instances) };

        try {
            db.update(TBL_NAME_SURVEY_INSTANCE, initialValues, "ID=?", where);
        }catch(Exception e){
            String ee= e.toString();
        }

        return 1;
    }

    /**
     * Delete survey instance details
     * @param db
     * @param instances
     */
    private void deleteSurveyInstanceDetail(SQLiteDatabase db, Long instances){

        String[] where = new String[] { String.valueOf(instances) };
        db.delete(TBL_NAME_SURVEY_INSTANCE_DETAIL, "ID_INSTANCE=?", where);
    }



    /**
     * Insert or update responses from login process
     * @param responses list of responses to insert
     * @param db db conection
     */
    private void synchronizeResponsesAttributes(List<ResponseAttribute> responses, Long question,SQLiteDatabase db){

        if(responses != null) {
            for (ResponseAttribute response : responses) {

                ContentValues initialValues = new ContentValues();

                initialValues.put("ID", response.getInput_id());
                initialValues.put("LABEL", response.getLabel());
                initialValues.put("TYPE", response.getType());
                initialValues.put("QUESTION", question);

                // Inserta o actualiza un registro
                if ((int) db.insertWithOnConflict(TBL_NAME_RESPONSE_ATTRIBUTES, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
                    db.update(TBL_NAME_RESPONSE_ATTRIBUTES, initialValues, "ID=?", new String[]{String.valueOf(response.getInput_id())});
                }

            }
        }
    }

}
