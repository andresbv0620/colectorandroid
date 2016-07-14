package co.colector.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import co.colector.R;
import co.colector.model.response.ErrorResponse;
import co.colector.session.AppSession;

/**
 * Clase generica para el consumo de servicios del API Rest </br>
 * 
 * @author dherrera
 * 
 */
public class BackgroundTask extends AsyncTask<String, Integer, Object> {

	private boolean isService;
	private ProgressDialog pDialogs;
	private Object param;
	private Object responseClass;
	private AsyncResponse response;
	private HttpRequest request;
	private String id_instances;
	private final Gson gson = new Gson();

	public BackgroundTask(Context context, Object param, Object responseClass, AsyncResponse response, String id_instance, Boolean isService) {

		if (!isService) {
			pDialogs = new ProgressDialog(context);
			pDialogs.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialogs.setMessage(context.getString(R.string.generalProgressDialogMessage));
			pDialogs.setCancelable(false);
		}

		this.isService = isService;
		this.param = param;
		this.responseClass = responseClass;
		this.response = response;
		this.id_instances = id_instance;
	}

	@Override
	protected Object doInBackground(String... params) {
		Object toReturn = new Object();
		
		try {
			request = HttpRequest.post(params[0]);
			// Headers
			request.contentType("application/json");
			request.acceptCharset("application/json");
			if(AppSession.getInstance().getUser() != null) {
				String token = AppSession.getInstance().getUser().getToken();
				request.header("token", token);

				// SSL Configuration
				request.trustAllCerts();
				request.trustAllHosts();
			}
			// Body
				if (param != null) {
					request.send(gson.toJson(param));
				}

				String response = request.body();

				if (request.ok() || request.created()) {
					try {
						toReturn = gson.fromJson(response, responseClass.getClass());
					}catch (Exception e){
						String testt = "err" + e;
						toReturn = new ErrorResponse(001, "no reconocido");
					}finally{

					}
					} else if (request.serverError()) {
					toReturn = gson.fromJson(response, ErrorResponse.class);
				} else if (request.unathorizedRequest()) {
					toReturn = new ErrorResponse(401, "no autorizado");
				}

		} catch (HttpRequest.HttpRequestException e) {
			toReturn = new ErrorResponse(404, e.getMessage());
		}
		return toReturn;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		int progreso = values[0].intValue();
		if (!isService)
			pDialogs.setProgress(progreso);
	}

	@Override
	protected void onPreExecute() {
		if (!isService)
			pDialogs.show();
	}

	@Override
	protected void onPostExecute(Object result) {
		if (!isService)
			pDialogs.dismiss();

		if (response != null) {
			response.callback(result,id_instances);
		}
	}

	@Override
	protected void onCancelled() {
		if (!isService)
			pDialogs.dismiss();
	}

}
