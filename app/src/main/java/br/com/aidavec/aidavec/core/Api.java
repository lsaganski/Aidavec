package br.com.aidavec.aidavec.core;

/**
 * Created by Leonardo Saganski on 13/12/16.
 */

import android.os.Handler;
import android.provider.Settings;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.aidavec.aidavec.fragments.HomeFrag;
import br.com.aidavec.aidavec.fragments.NoteDetailsFrag;
import br.com.aidavec.aidavec.fragments.NoteFrag;
import br.com.aidavec.aidavec.fragments.ReportFrag;
import br.com.aidavec.aidavec.fragments.ReportMonthFrag;
import br.com.aidavec.aidavec.fragments.ReportWeekFrag;
import br.com.aidavec.aidavec.helpers.PhotoMultipartRequest;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.helpers.VolleyHelper;
import br.com.aidavec.aidavec.models.Answers;
import br.com.aidavec.aidavec.models.ChartHome;
import br.com.aidavec.aidavec.models.ChartSemanal;
import br.com.aidavec.aidavec.models.Logg;
import br.com.aidavec.aidavec.models.Note;
import br.com.aidavec.aidavec.models.ReportData;
import br.com.aidavec.aidavec.models.User;
import br.com.aidavec.aidavec.models.Vehicle;
import br.com.aidavec.aidavec.models.Waypoint;

public class Api implements Response.ErrorListener {

    JsonObjectRequest jsonObjReq;
    JsonArrayRequest jsonArrayReq;
    int what;
    Gson gson;
    Handler handler;

    boolean ins;

    private static Api instance;

    public static Api getInstance() {
        if (instance == null)
            instance = new Api();

        return instance;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        if (error != null) {
            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                Utils.Show("Sem conexão à internet.", true);  // Timeout error
            } else if (error instanceof AuthFailureError) {
                Utils.Show("Erro na autenticação : " + error.getMessage(), true);  // Auth error
            } else if (error instanceof ServerError) {
                Utils.Show("Erro no servidor : " + error.getMessage(), true);   // server error
            } else if (error instanceof NetworkError) {
                Utils.Show("Sem internet : " + error.getMessage(), true);  // network error
            } else if (error instanceof ParseError) {
                Utils.Show("Erro de Conversão : " + error.getMessage(), true);  // Parse Error
            }

            if (error.networkResponse != null) {
                if (error.networkResponse.statusCode == 404) {
                    Utils.Show("Erro no servidor : " + error.getMessage(), true);  // 404 error
                }
            }
        } else {
            Utils.Show("Erro desconhecido : " + error.getMessage(), true);
        }

        if (handler != null)
            handler.sendEmptyMessage(0);

    }

    public void Login(final Handler h, final String username, final String password) {
        try {
           // handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
                if (h != null)
                    h.sendEmptyMessage(9);
            } else {
                String path = Globals.getInstance().apiPath + "login";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 1) {
                                        JSONArray jArr = new JSONArray(response);
                                        if (jArr != null && jArr.length() > 0) {
                                            JSONObject jObj = (JSONObject) jArr.get(0);
                                            User user = FillUserWithJSON(jObj);
                                            if (user == null) {
                                                h.sendEmptyMessage(0);
                                            } else if (user.getUsr_status() == 0) {
                                                h.sendEmptyMessage(2);
                                            } else {
                                                Globals.getInstance().loggedUser = user;
                                                h.sendEmptyMessage(1);
                                            }
                                        } else {
                                            h.sendEmptyMessage(0);
                                        }
                                    }
                                } catch (JSONException e) {
                                    Utils.getInstance().saveLog("Api - LoginResponse", e.getMessage());
                                    h.sendEmptyMessage(9);
                                    Utils.Show("API Error (JSon) Login : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("username", username);
                        params.put("password", Utils.HashMD5(password));
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - Login", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error Login : " + e.getMessage(), true);
        }
    }

    public void CheckEmailExists(final Handler h, final String email) {
        try {
        //    handler = h;

            if (Globals.getInstance().loggedUser != null && Globals.getInstance().loggedUser.getUsr_id() > 0)
                h.sendEmptyMessage(1);

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "checkemail";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 1) {
                                        JSONArray jArr = new JSONArray(response);
                                        if (jArr != null && jArr.length() > 0) {
                                            JSONObject jObj = (JSONObject) jArr.get(0);
                                            User user = FillUserWithJSON(jObj);
                                            if (user == null) {
                                                h.sendEmptyMessage(1);
                                            } else {
                                                h.sendEmptyMessage(0);
                                            }
                                        } else {
                                            h.sendEmptyMessage(1);
                                        }
                                    }
                                } catch (JSONException e) {
                                    Utils.getInstance().saveLog("Api - CheckMail Response", e.getMessage());
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) Check Email : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("email", email);
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - CheckMail", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error Login : " + e.getMessage(), true);
        }
    }

    public void GetNotes(final Handler h) {
        try {
        //    handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "getnotes";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 1) {
                                        JSONArray jArr = new JSONArray(response);
                                        List<Note> list = FillArrayNoteWithJSON(jArr);
                                        Collections.reverse(list);
                                        NoteFrag.listObj = list;

                                        if (h != null)
                                            h.sendEmptyMessage(1);
                                    }
                                } catch (JSONException e) {
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) GetNotes : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("USR_ID", String.valueOf(Globals.getInstance().loggedUser.getUsr_id()));
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            if (Globals.getInstance().devMode)
                Utils.Show("API Error GetNotes : " + e.getMessage(), true);
        }
    }

    public void GetAnswers(final Handler h, final int not_id) {
        try {
            //    handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "getanswers";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 1) {
                                        JSONArray jArr = new JSONArray(response);
                                        List<Answers> list = FillArrayAnswersWithJSON(jArr);
                                        NoteDetailsFrag.listObjAnswers = list;

                                        if (h != null)
                                            h.sendEmptyMessage(1);
                                    }
                                } catch (JSONException e) {
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) GetAnswers : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("NOT_ID", String.valueOf(not_id));
                        params.put("USR_ID", String.valueOf(Globals.getInstance().loggedUser.getUsr_id()));
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            if (Globals.getInstance().devMode)
                Utils.Show("API Error GetAnswers : " + e.getMessage(), true);
        }
    }

    public void GetVehicle(final Handler h) {
        try {
           // handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "getvehicle";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 1) {
                                        JSONArray jArr = new JSONArray(response);
                                        if (jArr != null && jArr.length() > 0) {
                                            JSONObject jObj = (JSONObject) jArr.get(0);
                                            Vehicle obj = FillVehicleWithJSON(jObj);
                                            if (obj == null) {
                                                if (h != null)
                                                    h.sendEmptyMessage(1);
                                            } else {
                                                Globals.getInstance().loggedVehicle = obj;
                                                Globals.getInstance().saveVehicleInPrefs();

                                                if (h != null)
                                                    h.sendEmptyMessage(1);
                                            }
                                        } else {
                                            if (h != null)
                                                h.sendEmptyMessage(1);
                                        }
                                    }
                                } catch (JSONException e) {
                                    Utils.getInstance().saveLog("Api - getVehicleResponse", e.getMessage());
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) GetVehicle : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("USR_ID", String.valueOf(Globals.getInstance().loggedUser.getUsr_id()));
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - getVehicle", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error GetVehicle : " + e.getMessage(), true);
        }
    }

    public void GetUser(final Handler h) {
        try {
         //   handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "getuser";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 1) {
                                        JSONArray jArr = new JSONArray(response);
                                        if (jArr != null && jArr.length() > 0) {
                                            JSONObject jObj = (JSONObject) jArr.get(0);
                                            User obj = FillUserWithJSON(jObj);
                                            if (obj == null) {
                                                h.sendEmptyMessage(1);
                                            } else {
                                                Globals.getInstance().loggedUser = obj;
                                                Globals.getInstance().saveUserInPrefs();
                                                h.sendEmptyMessage(1);
                                            }
                                        } else {
                                            h.sendEmptyMessage(1);
                                        }
                                    }
                                } catch (JSONException e) {
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) GetUser : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("USR_ID", String.valueOf(Globals.getInstance().loggedUser.getUsr_id()));
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            if (Globals.getInstance().devMode)
                Utils.Show("API Error GetUser : " + e.getMessage(), true);
        }
    }

    public void GetLastWaypoint(final Handler h) {
        try {
//            this.handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "getwaypoint";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        JSONArray jArr = new JSONArray(response);
                                        if (jArr != null && jArr.length() > 0) {
                                            JSONObject jObj = (JSONObject) jArr.get(0);
                                            Waypoint obj = FillWaypointWithJSON(jObj);
                                            if (obj != null) {
                                                Globals.getInstance().lastWaypointCreated = obj;
                                                //if (obj.getWay_percorrido() > 0)
                                                    Globals.getInstance().lastWaypointWithRegisteredDistance = obj;
                                            }
                                        }
                                    }
                                    h.sendEmptyMessage(0);
                                } catch (JSONException e) {
                                    Utils.getInstance().saveLog("Api - getLatWaypoint response", e.getMessage());
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) GetWaypoint : " + e.getMessage(), true);
                                    h.sendEmptyMessage(0);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("USR_ID", String.valueOf(Globals.getInstance().loggedUser.getUsr_id()));
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - getLastWaypoint", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error GetWaypoint : " + e.getMessage(), true);
        }
    }

    public void GetChartSemanal(final Handler h) {
        try {
            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "getchartsemanal/";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        JSONArray jArr = new JSONArray(response);
                                        List<ChartSemanal> list = FillArrayChartSemanalWithJSON(jArr);
                                        ReportWeekFrag.listObjChart = list;

                                        if (h != null)
                                            h.sendEmptyMessage(1);
                                    }
                                } catch (JSONException e) {
                                    Utils.getInstance().saveLog("Api - getChartSeamnal response", e.getMessage());
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) GetChartSemanal : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        String id = String.valueOf(Globals.getInstance().loggedUser.getUsr_id());
                        params.put("USR_ID", id);
                        String now = Utils.getInstance().getStringNow();
                        params.put("DT_TODAY", now);
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - getChartSemanal", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error GetChartSemanal : " + e.getMessage(), true);
        }
    }

    public void GetChartHome(final Handler h) {
        try {
            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "getcharthome/";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        JSONArray jArr = new JSONArray(response);
                                        List<ChartHome> list = FillArrayChartHomeWithJSON(jArr);
                                        ReportMonthFrag.listObjChart = list;

                                        if (h != null)
                                            h.sendEmptyMessage(1);
                                    }
                                } catch (JSONException e) {
                                    Utils.getInstance().saveLog("Api - getChartHome response", e.getMessage());
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) GetChartHome : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("USR_ID", String.valueOf(Globals.getInstance().loggedUser.getUsr_id()));
                        String now = Utils.getInstance().getStringNow();
                        params.put("DT_TODAY", now);
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - getChartHome", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error GetChartHome : " + e.getMessage(), true);
        }
    }

    public void GetReport(final Handler h) {
        try {

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "report";
                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        JSONArray jArr = new JSONArray(response);
                                        List<ReportData> list = FillArrayReportDataWithJSON(jArr);
                                        ReportFrag.listObjData = list;

                                        if (h != null)
                                            h.sendEmptyMessage(1);
                                    }
                                } catch (JSONException e) {
                                    Utils.getInstance().saveLog("Api - getReport response", e.getMessage());
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) GetReport : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("USR_ID", String.valueOf(Globals.getInstance().loggedUser.getUsr_id()));
                        String now = Utils.getInstance().getStringNow();
                        params.put("DT_TODAY", now);
                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - getResport", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error GetReport : " + e.getMessage(), true);
        }
    }

    public void SaveUser(final Handler h, final User obj) {
        try {
         //   handler = h;
            ins = true;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "user";
                if (obj.getUsr_id() > 0)
                    ins = false;

                int verb = Request.Method.POST;

                if (!ins) {
                    verb = Request.Method.PUT;
                }


                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        JSONObject jObj = new JSONObject(response);
                                        if (ins && jObj.has("insertId") && jObj.getInt("insertId") > 0) {
                                            int id = jObj.getInt("insertId");
                                            obj.setUsr_id(id);
                                            Globals.getInstance().loggedUser = obj;
                                            if (h != null)
                                                h.sendEmptyMessage(1);
                                        } else if (!ins && jObj.has("affectedRows") && jObj.getInt("affectedRows") > 0) {
                                            Globals.getInstance().loggedUser = obj;
                                            Globals.getInstance().saveUserInPrefs();
                                            Globals.getInstance().handlerUI.sendEmptyMessage(102); // Atualiza header do navigation drawer
                                            if (h != null)
                                                h.sendEmptyMessage(1);
                                        } else if (!jObj.has("success")) {
                                            if (h != null)
                                                h.sendEmptyMessage(0);
                                        }
                                    }
                                } catch (JSONException e) {
                                    if (h != null)
                                        h.sendEmptyMessage(0);
                                    Utils.getInstance().saveLog("Api - saveUser response", e.getMessage());
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) Login : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();

                        /*if (ins){
                            params.put("usuario", "admin");
                            params.put("senha", "81dc9bdb52d04dc20036dbd8313ed055");
                        } else {
                            params.put("usuario", Utils.loggedUser.getUser());
                            params.put("senha", Utils.loggedUser.getPassword());
                        }*/

                        if (!ins)
                            params.put("USR_ID", String.valueOf(obj.getUsr_id()));

                        params.put("USR_NOME", Utils.DefStrVal(obj.getUsr_nome(), ""));
                        params.put("USR_SOBRENOME", Utils.DefStrVal(obj.getUsr_sobrenome(), ""));
                        params.put("USR_EMAIL", Utils.DefStrVal(obj.getUsr_email(), ""));
                        params.put("USR_TELEFONE", Utils.DefStrVal(obj.getUsr_telefone(), ""));
                        params.put("USR_UF", Utils.DefStrVal(obj.getUsr_uf(), ""));
                        params.put("USR_CIDADE", Utils.DefStrVal(obj.getUsr_cidade(), ""));
                        if (ins || (obj.getUsr_senha() != null && obj.getUsr_senha().length() > 0))
                            params.put("USR_SENHA", Utils.DefStrVal(Utils.HashMD5(Utils.DefStrVal(obj.getUsr_senha(), "")), ""));
                        if (!ins && !Globals.getInstance().loggedUser.getUsr_email().equals(obj.getUsr_email()))
                            params.put("USR_STATUS", "0");
                        else
                            params.put("USR_STATUS", String.valueOf(obj.getUsr_status()));
                        params.put("USR_DEVICE", Utils.DefStrVal(obj.getUsr_device(), ""));

                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - saveUser", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error SaveUser : " + e.getMessage(), true);
        }
    }

    public void SaveVehicle(final Handler h, final Vehicle obj) {
        try {
          //  handler = h;
            ins = true;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "vehicle";
                if (obj.getVei_id() > 0)
                    ins = false;

                int verb = Request.Method.POST;

                if (!ins) {
                    verb = Request.Method.PUT;
                }


                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        JSONObject jObj = new JSONObject(response);
                                        if (ins && jObj.has("insertId") && jObj.getInt("insertId") > 0) {
                                            int id = jObj.getInt("insertId");
                                            obj.setVei_id(id);
                                            Globals.getInstance().loggedVehicle = obj;
                                            Globals.getInstance().saveVehicleInPrefs();
                                            if (h != null)
                                                h.sendEmptyMessage(1);
                                        } else if (!ins && jObj.has("affectedRows") && jObj.getInt("affectedRows") > 0) {
                                            Globals.getInstance().loggedVehicle = obj;
                                            Globals.getInstance().saveVehicleInPrefs();
                                            if (h != null)
                                                h.sendEmptyMessage(1);
                                        } else {
                                            if (h != null)
                                                h.sendEmptyMessage(0);
                                        }
                                    }
                                } catch (JSONException e) {
                                    if (h != null)
                                        h.sendEmptyMessage(0);
                                    Utils.getInstance().saveLog("Api - saveVehicle response", e.getMessage());
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) SaveVehicle : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();

                        params.put("USR_ID", String.valueOf(Globals.getInstance().loggedUser.getUsr_id()));
                        params.put("VEI_MARCA", Utils.DefStrVal(obj.getVei_marca(), ""));
                        params.put("VEI_MODELO", Utils.DefStrVal(obj.getVei_modelo(), ""));
                        params.put("VEI_COR", Utils.DefStrVal(obj.getVei_cor(), ""));
                        params.put("VEI_ANO", Utils.DefStrVal(obj.getVei_ano(), ""));
                        params.put("VEI_COBERTURA", Utils.DefStrVal(obj.getVei_cobertura(), ""));
                        params.put("VEI_STATUS", String.valueOf(obj.getVei_status()));
                        params.put("VEI_OKFOTOA", String.valueOf(obj.getVei_okfotoa()));
                        params.put("VEI_OKFOTOB", String.valueOf(obj.getVei_okfotob()));
                        params.put("VEI_OKFOTOC", String.valueOf(obj.getVei_okfotoc()));
                        if (!ins) {
                            params.put("VEI_ID", String.valueOf(obj.getVei_id()));
                        }

                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - saveVhicle ", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error SaveVehicle : " + e.getMessage(), true);
        }
    }

    public void SaveNote(final Handler h, final Note obj) {
        try {
            //handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "note";

                int verb = Request.Method.PUT;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        if (h != null)
                                            h.sendEmptyMessage(0);
                                    }
                                } catch (Exception e) {
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) SaveNote : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();

                        params.put("NOT_ID", String.valueOf(obj.getNot_id()));
                        params.put("USR_ID", String.valueOf(obj.getUsr_id()));
                        params.put("NOT_TITULO", Utils.DefStrVal(obj.getNot_titulo(), ""));
                        params.put("NOT_MENSAGEM", Utils.DefStrVal(obj.getNot_mensagem(), ""));
                        params.put("NOT_TIPO", Utils.DefStrVal(obj.getNot_tipo(), ""));
                        params.put("NOT_OPCAOA", Utils.DefStrVal(obj.getNot_opcaoa(), ""));
                        params.put("NOT_OPCAOB", Utils.DefStrVal(obj.getNot_opcaob(), ""));
                        params.put("NOT_OPCAOC", Utils.DefStrVal(obj.getNot_opcaoc(), ""));
                        params.put("NOT_OPCAOD", Utils.DefStrVal(obj.getNot_opcaod(), ""));
                        params.put("NOT_OPCAOE", Utils.DefStrVal(obj.getNot_opcaoe(), ""));
                        params.put("NOT_PUSH", Utils.DefStrVal(String.valueOf(obj.getNot_push()), ""));

                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            if (Globals.getInstance().devMode)
                Utils.Show("API Error SaveNote : " + e.getMessage(), true);
        }
    }

    public void SaveAnswer(final Handler h, final Answers obj) {
        try {
            //handler = h;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "answer";

                int verb = Request.Method.PUT;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        if (h != null)
                                            h.sendEmptyMessage(0);
                                    }
                                } catch (Exception e) {
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) SaveAnswer : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();

                        params.put("NOT_ID", String.valueOf(obj.getNot_id()));
                        params.put("USR_ID", String.valueOf(obj.getUsr_id()));
                        params.put("RES_RESPOSTA", Utils.DefStrVal(obj.getRes_resposta(), ""));

                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            if (Globals.getInstance().devMode)
                Utils.Show("API Error SaveAnswer : " + e.getMessage(), true);
        }
    }

    public void SaveLog(final Logg obj) {
        try {
            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "log";

                int verb = Request.Method.POST;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                } catch (Exception e) {
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) SaveLog : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();

                        obj.setLog_info(Utils.getInstance().getIPAddress(true));

                        params.put("USR_ID", String.valueOf(obj.getUsr_id()));
                        params.put("LOG_DATA", Utils.DefStrVal(obj.getLog_data(), ""));
                        params.put("LOG_TIPO", Utils.DefStrVal(obj.getLog_tipo(), ""));
                        params.put("LOG_INFO", Utils.DefStrVal(obj.getLog_info(), ""));

                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            if (Globals.getInstance().devMode)
                Utils.Show("API Error SaveLog : " + e.getMessage(), true);
        }
    }

    public void SaveWaypoints(final List<Waypoint> waypoints, final Handler h) {
        try {
            //handler = h;
            ins = true;

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "waypoints";

                int verb = Request.Method.POST;

                String strArr = Globals.getInstance().gson.toJson(waypoints, new TypeToken<ArrayList<Waypoint>>() {}.getType());

                strArr = "{ 'waypoints' : " + strArr + "}";

                JSONObject arr = new JSONObject(strArr);

                JsonObjectRequest req = new JsonObjectRequest(verb, path, arr,

                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String resp = response.toString();
                                    Globals.getInstance().db.deleteWaypoints(waypoints) ;
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("Sincronizados " + waypoints.size(), false);
                                    h.sendEmptyMessage(0);
                                    // Apos enviar os dados para o server, chamar o handler abaixo para atualizar os totais na Home com dados do server
                                    Globals.getInstance().handlerUIHome.sendEmptyMessage(101);
                                } catch (Exception e) {
                                    Utils.getInstance().saveLog("Api - saveWaypoint response", e.getMessage());
                                    h.sendEmptyMessage(0);
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) SaveWaypoint : " + e.getMessage(), true);
                                }
                            }
                        }, this){};

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - saveWaypoint", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error SaveWaypoint : " + e.getMessage(), true);
        }
    }

    public void ChangePassword(final Handler h, final String pass) {
        try {

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "password";

                int verb = Request.Method.PUT;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        h.sendEmptyMessage(1);
                                    }
                                } catch (Exception e) {
                                    h.sendEmptyMessage(0);
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) ChangePassword : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();

                        params.put("USR_ID", String.valueOf(Globals.getInstance().loggedUser.getUsr_id()));
                        params.put("USR_SENHA", Utils.DefStrVal(Utils.HashMD5(Utils.DefStrVal(pass, "")), ""));

                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            if (Globals.getInstance().devMode)
                Utils.Show("API Error ChangePassword : " + e.getMessage(), true);
        }
    }

    public void ForgotPassword(final Handler h, final String email) {
        try {

            if (!Utils.verificaConexao()) {
                Utils.Show("Sem conexão.", true);
            } else {
                String path = Globals.getInstance().apiPath + "forgot";

                int verb = Request.Method.PUT;

                StringRequest req = new StringRequest(verb, path,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.length() > 0) {
                                        h.sendEmptyMessage(1);
                                    }
                                } catch (Exception e) {
                                    h.sendEmptyMessage(0);
                                    if (Globals.getInstance().devMode)
                                        Utils.Show("API Error (JSon) ForgotPAssword : " + e.getMessage(), true);
                                }
                            }
                        }, this){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();

                        params.put("USR_EMAIL", Utils.DefStrVal(email, ""));

                        return params;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                VolleyHelper.getInstance().addToRequestQueue(req, "tag");
            }
        } catch (Exception e) {
            if (Globals.getInstance().devMode)
                Utils.Show("API Error ForgotPAssword : " + e.getMessage(), true);
        }
    }

    public void Upload(final Object obj, String filenName, final Handler h) {
        try {
            //handler = h;
            String path = Globals.getInstance().apiPath + "upload";

            PhotoMultipartRequest imageUploadReq = new PhotoMultipartRequest(path,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String a = error.getMessage();
                            h.sendEmptyMessage(0);
                        }
                    },
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            h.sendEmptyMessage(1);
                        }
                    }, (byte[]) obj, filenName);

            imageUploadReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleyHelper.getInstance().addToRequestQueue(imageUploadReq, "any_tag");
        } catch (Exception e) {
            Utils.getInstance().saveLog("Api - Upload", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("API Error Upload : " + e.getMessage(), true);
        }
    }

    //--------------------------------------------

    public List<Note> FillArrayNoteWithJSON(JSONArray arr) {
        List<Note> list = new ArrayList<Note>();
        Note newObj;

        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);

                newObj = FillNoteWithJSON(obj);
                list.add(newObj);
            }
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillArrayNoteWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillArrayNoteWithJSON - Erro JSON : " + e.getMessage(), true);
        }

        return list;
    }

    public Note FillNoteWithJSON(JSONObject obj) {
        Note newObj = new Note();

        try {
            if (!obj.isNull("NOT_ID"))
                newObj.setNot_id(obj.getInt("NOT_ID"));
            if (!obj.isNull("USR_ID"))
                newObj.setUsr_id(obj.getInt("USR_ID"));
            if (!obj.isNull("NOT_MENSAGEM"))
                newObj.setNot_mensagem(obj.getString("NOT_MENSAGEM"));
            if (!obj.isNull("NOT_TITULO"))
                newObj.setNot_titulo(obj.getString("NOT_TITULO"));
            if (!obj.isNull("NOT_TIPO"))
                newObj.setNot_tipo(obj.getString("NOT_TIPO"));
            if (!obj.isNull("NOT_PUSH"))
                newObj.setNot_push(obj.getInt("NOT_PUSH"));
            if (!obj.isNull("NOT_OPCAOA"))
                newObj.setNot_opcaoa(obj.getString("NOT_OPCAOA"));
            if (!obj.isNull("NOT_OPCAOB"))
                newObj.setNot_opcaob(obj.getString("NOT_OPCAOB"));
            if (!obj.isNull("NOT_OPCAOC"))
                newObj.setNot_opcaoc(obj.getString("NOT_OPCAOC"));
            if (!obj.isNull("NOT_OPCAOD"))
                newObj.setNot_opcaod(obj.getString("NOT_OPCAOD"));
            if (!obj.isNull("NOT_OPCAOE"))
                newObj.setNot_opcaoe(obj.getString("NOT_OPCAOE"));
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillNoteWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillNoteWithJSON - Erro JSON", true);
        }

        return newObj;
    }

    public List<Answers> FillArrayAnswersWithJSON(JSONArray arr) {
        List<Answers> list = new ArrayList<Answers>();
        Answers newObj;

        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);

                newObj = FillAnswersWithJSON(obj);
                list.add(newObj);
            }
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillArrayAnswersWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillArrayAnswersWithJSON - Erro JSON : " + e.getMessage(), true);
        }

        return list;
    }

    public Answers FillAnswersWithJSON(JSONObject obj) {
        Answers newObj = new Answers();

        try {
            if (!obj.isNull("NOT_ID"))
                newObj.setNot_id(obj.getInt("NOT_ID"));
            if (!obj.isNull("USR_ID"))
                newObj.setUsr_id(obj.getInt("USR_ID"));
            if (!obj.isNull("RES_RESPOSTA"))
                newObj.setRes_resposta(obj.getString("RES_RESPOSTA"));
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillAnswersWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillAnswersWithJSON - Erro JSON", true);
        }

        return newObj;
    }

    public List<ChartSemanal> FillArrayChartSemanalWithJSON(JSONArray arr) {
        List<ChartSemanal> list = new ArrayList<ChartSemanal>();
        ChartSemanal newObj;

        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);

                newObj = FillChartSemanalWithJSON(obj);
                list.add(newObj);
            }
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillArrayChartSemanalWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillArrayChartSemanalWithJSON - Erro JSON : " + e.getMessage(), true);
        }

        return list;
    }

    public ChartSemanal FillChartSemanalWithJSON(JSONObject obj) {
        ChartSemanal newObj = new ChartSemanal();

        try {
            if (!obj.isNull("seg"))
                newObj.setSegunda(obj.getDouble("seg"));
            if (!obj.isNull("ter"))
                newObj.setTerca(obj.getDouble("ter"));
            if (!obj.isNull("qua"))
                newObj.setQuarta(obj.getDouble("qua"));
            if (!obj.isNull("qui"))
                newObj.setQuinta(obj.getDouble("qui"));
            if (!obj.isNull("sex"))
                newObj.setSexta(obj.getDouble("sex"));
            if (!obj.isNull("sab"))
                newObj.setSabado(obj.getDouble("sab"));
            if (!obj.isNull("dom"))
                newObj.setDomingo(obj.getDouble("dom"));
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillChartSemanalWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillChartSemanalWithJSON - Erro JSON", true);
        }

        return newObj;
    }

    public List<ChartHome> FillArrayChartHomeWithJSON(JSONArray arr) {
        List<ChartHome> list = new ArrayList<ChartHome>();
        ChartHome newObj;

        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);

                newObj = FillChartHomeWithJSON(obj);
                list.add(newObj);
            }
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillArrayChartHomeWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillArrayChartHomeWithJSON - Erro JSON : " + e.getMessage(), true);
        }

        return list;
    }

    public ChartHome FillChartHomeWithJSON(JSONObject obj) {
        ChartHome newObj = new ChartHome();

        try {
            if (!obj.isNull("ca") &&
                    !obj.isNull("cb") &&
                    !obj.isNull("cc") &&
                    !obj.isNull("cd") &&
                    !obj.isNull("ce") &&
                    !obj.isNull("cf") &&
                    !obj.isNull("cg") &&
                    !obj.isNull("ch")  ) {
                double[] v = new double[] {
                        obj.getDouble("ca"),
                        obj.getDouble("cb"),
                        obj.getDouble("cc"),
                        obj.getDouble("cd"),
                        obj.getDouble("ce"),
                        obj.getDouble("cf"),
                        obj.getDouble("cg"),
                        obj.getDouble("ch")
                };

                newObj.setVals(v);

            }
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillChartHomeWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillChartHomeWithJSON - Erro JSON", true);
        }

        return newObj;
    }

    public List<ReportData> FillArrayReportDataWithJSON(JSONArray arr) {
        List<ReportData> list = new ArrayList<ReportData>();
        ReportData newObj;

        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);

                newObj = FillReportDataWithJSON(obj);
                list.add(newObj);
            }
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillArrayReportDataWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillArrayReportDataWithJSON - Erro JSON : " + e.getMessage(), true);
        }

        return list;
    }

    public ReportData FillReportDataWithJSON(JSONObject obj) {
        ReportData newObj = new ReportData();

        try {
            if (!obj.isNull("total_pontos"))
                newObj.setTotalPontos(obj.getDouble("total_pontos"));
            if (!obj.isNull("total_pontos_campanha"))
                newObj.setTotalPontosCampanha(obj.getDouble("total_pontos_campanha"));
            if (!obj.isNull("km_dia"))
                newObj.setKmDia(obj.getDouble("km_dia"));
            if (!obj.isNull("km_semana"))
                newObj.setKmSemana(obj.getDouble("km_semana"));
            if (!obj.isNull("km_mes"))
                newObj.setKmMes(obj.getDouble("km_mes"));
        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillReportDataWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillReportDataWithJSON - Erro JSON", true);
        }

        return newObj;
    }

    public User FillUserWithJSON(JSONObject obj) {
        User newObj = new User();

        try {
            if (!obj.isNull("USR_ID"))
                newObj.setUsr_id(obj.getInt("USR_ID"));
            if (!obj.isNull("USR_NOME"))
                newObj.setUsr_nome(obj.getString("USR_NOME"));
            if (!obj.isNull("USR_SOBRENOME"))
                newObj.setUsr_sobrenome(obj.getString("USR_SOBRENOME"));
            if (!obj.isNull("USR_EMAIL"))
                newObj.setUsr_email(obj.getString("USR_EMAIL"));
            if (!obj.isNull("USR_TELEFONE"))
                newObj.setUsr_telefone(obj.getString("USR_TELEFONE"));
            if (!obj.isNull("USR_UF"))
                newObj.setUsr_uf(obj.getString("USR_UF"));
            if (!obj.isNull("USR_CIDADE"))
                newObj.setUsr_cidade(obj.getString("USR_CIDADE"));
            if (!obj.isNull("USR_DEVICE"))
                newObj.setUsr_device(obj.getString("USR_DEVICE"));
            if (!obj.isNull("USR_STATUS"))
                newObj.setUsr_status(obj.getInt("USR_STATUS"));

        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillUserWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillUserWithJSON - Erro JSON", true);
        }

        return newObj;
    }

    public Vehicle FillVehicleWithJSON(JSONObject obj) {
        Vehicle newObj = new Vehicle();

        try {
            if (!obj.isNull("USR_ID"))
                newObj.setUsr_id(obj.getInt("USR_ID"));
            if (!obj.isNull("VEI_ID"))
                newObj.setVei_id(obj.getInt("VEI_ID"));
            if (!obj.isNull("VEI_MARCA"))
                newObj.setVei_marca(obj.getString("VEI_MARCA"));
            if (!obj.isNull("VEI_MODELO"))
                newObj.setVei_modelo(obj.getString("VEI_MODELO"));
            if (!obj.isNull("VEI_ANO"))
                newObj.setVei_ano(obj.getString("VEI_ANO"));
            if (!obj.isNull("VEI_COR"))
                newObj.setVei_cor(obj.getString("VEI_COR"));
            if (!obj.isNull("VEI_COBERTURA"))
                newObj.setVei_cobertura(obj.getString("VEI_COBERTURA"));
            if (!obj.isNull("VEI_OKFOTOA"))
                newObj.setVei_okfotoa(obj.getInt("VEI_OKFOTOA"));
            if (!obj.isNull("VEI_OKFOTOB"))
                newObj.setVei_okfotob(obj.getInt("VEI_OKFOTOB"));
            if (!obj.isNull("VEI_OKFOTOC"))
                newObj.setVei_okfotoc(obj.getInt("VEI_OKFOTOC"));
            if (!obj.isNull("VEI_STATUS"))
                newObj.setVei_status(obj.getInt("VEI_STATUS"));

        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillVehicleWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillVehicleWithJSON - Erro JSON", true);
        }

        return newObj;
    }

    public Waypoint FillWaypointWithJSON(JSONObject obj) {
        Waypoint newObj = new Waypoint();

        try {
            if (!obj.isNull("WAY_ID"))
                newObj.setWay_id(obj.getInt("WAY_ID"));
            if (!obj.isNull("USR_ID"))
                newObj.setUsr_id(obj.getInt("USR_ID"));
            if (!obj.isNull("WAY_LATITUDE"))
                newObj.setWay_latitude(obj.getDouble("WAY_LATITUDE"));
            if (!obj.isNull("WAY_LONGITUDE"))
                newObj.setWay_longitude(obj.getDouble("WAY_LONGITUDE"));
            if (!obj.isNull("WAY_DATE"))
                newObj.setWay_date(obj.getString("WAY_DATE"));
            if (!obj.isNull("WAY_PERCORRIDO"))
                newObj.setWay_percorrido(obj.getDouble("WAY_PERCORRIDO"));

        } catch (JSONException e) {
            Utils.getInstance().saveLog("Api - FillWaypointWithJson", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show("FillVehicleWithJSON - Erro JSON", true);
        }

        return newObj;
    }
}
