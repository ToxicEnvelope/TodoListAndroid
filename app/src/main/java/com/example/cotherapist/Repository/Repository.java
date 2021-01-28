package com.example.cotherapist.Repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cotherapist.Model.Task;
import com.example.cotherapist.Model.Therapist.Therapist;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository extends BaseRepository {

    private final String SIGN_UP_URI = BASE_URL.concat("/accounts/sign-in");
    private final String LOGIN_URI = BASE_URL.concat("/accounts/login");
    private final String GET_TASK = BASE_URL.concat("/tasks/<task_id>");
    private final String GET_ALL_TASKS = BASE_URL.concat("/tasks");
    private final String UPLOAD_TASK = BASE_URL.concat("/tasks");
    private final String UPDATE_TASK = BASE_URL.concat("/tasks/");
    private final String DELETE_TASK = BASE_URL.concat("/tasks/");

    private static Repository mRepository;
    private Gson gson;
    private Context mContext;

    public static Repository getInstance(final Context context) {
        if (mRepository == null) {
            mRepository = new Repository(context);
        }
        return mRepository;
    }

    private Repository(final Context context) {
        this.mContext = context;
        gson = new Gson();
    }

    private RepositorySignUpInterface mSignUpListener;

    public void setSignUpListener(RepositorySignUpInterface repositoryLoginInterface) {
        this.mSignUpListener = repositoryLoginInterface;
    }

    private RepositoryLoginInterface mLoginListener;

    public void setLoginListener(RepositoryLoginInterface repositoryLoginInterface) {
        this.mLoginListener = repositoryLoginInterface;
    }

    private RepositoryDownloadTasksInterface mDownloadTasksListener;

    public void setDownloadTasksListener(RepositoryDownloadTasksInterface repositoryDownloadTasksInterface) {
        this.mDownloadTasksListener = repositoryDownloadTasksInterface;
    }

    //***--------------**//
    //Upload Task

    private RepositoryUploadTaskInterface mUploadTaskListener;

    public void setUploadTaskListener(RepositoryUploadTaskInterface repositoryUploadInterface) {
        this.mUploadTaskListener = repositoryUploadInterface;
    }

    private RepositoryUpdateTaskInterface mUpdateTaskListener;

    public void setUpdateTaskListener(RepositoryUpdateTaskInterface repositoryUpdateInterface) {
        this.mUpdateTaskListener = repositoryUpdateInterface;
    }

    //***--------------**//
    //Delete Task

    private RepositoryDeleteTaskInterface mDeleteTaskListener;

    public void setDeleteTaskListener(RepositoryDeleteTaskInterface repositoryDeleteInterface) {
        this.mDeleteTaskListener = repositoryDeleteInterface;
    }


    public void loginUser(final String email, final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        final HashMap<String, String> postParams = new HashMap<String, String>();

        postParams.put("userEmail", email);
        postParams.put("userPassword", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                LOGIN_URI, new JSONObject(postParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("TAG", response.toString());
                        try {
                            //Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_LONG).show();
                            if (mLoginListener != null) {
                                mLoginListener.onUserLoginSucceed();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("TAG", "Error: " + error.getMessage());
                Log.d(TAG, "onErrorResponse: error network");
                if (mLoginListener != null) {
                    Toast.makeText(mContext, "username or password isn't correct!", Toast.LENGTH_LONG).show();
                    mLoginListener.onUserLoginFailed();
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    JSONObject jsonResponse = new JSONObject(jsonString);

                    jsonResponse.put("headers", new JSONObject(response.headers));
                    setAuthKey(jsonResponse.getJSONObject("headers").getString("Authorization"));
                    Log.d(TAG, "parseNetworkResponse: " + getAuthKey());
                    return Response.success(jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        requestQueue.add(jsonObjReq);
    }

    public void signUpUser(Therapist therapist) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        setTherapist(therapist);
        final HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("userFullName", therapist.getFullName());
        postParams.put("userEmail", therapist.getEmail());
        postParams.put("userPassword", therapist.getPassword());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                SIGN_UP_URI, new JSONObject(postParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d("TAG", response.toString());
                        try {
                            Toast.makeText(mContext, "Thank you for your Sign UP", Toast.LENGTH_LONG).show();
                            if (mSignUpListener != null) {
                                mSignUpListener.onUserSignUpSucceed();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("TAG", "Error: " + error.getMessage());
                if (mSignUpListener != null) {
                    mSignUpListener.onUserSignUpFailed();
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    JSONObject jsonResponse = new JSONObject(jsonString);
                    jsonResponse.put("headers", new JSONObject(response.headers));
                    Log.d(TAG, "parseNetworkResponse: " + jsonResponse.getJSONObject("headers").getString("Authorization"));
                    setAuthKey(jsonResponse.getJSONObject("headers").getString("Authorization"));
                    return Response.success(jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        requestQueue.add(jsonObjReq);
    }

    public void getAllTasks() {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        final List<Task> tasksList = new ArrayList<>();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GET_ALL_TASKS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Task task = gson.fromJson(String.valueOf(jsonObject), Task.class);
                                tasksList.add(task);
                            }
                            if (mDownloadTasksListener != null) {
                                mDownloadTasksListener.onUserDownloadTasksSucceed(tasksList);
                            }

                        } catch (Exception e) {
                            if (mDownloadTasksListener != null) {
                                mDownloadTasksListener.onUserDownloadTasksFailed(e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error network");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", getAuthKey());
                return authHeader;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        requestQueue.add(jsonObjReq);
    }


    public void uploadNewTask(final String taskDescription) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        final HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("taskDescription", taskDescription);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                UPLOAD_TASK, new JSONObject(postParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: upload");
                            JSONObject jsonResponse = response.getJSONObject("data");
                            String taskCreationTime=jsonResponse.getString("taskCreationTime");
                            String taskId=jsonResponse.getString("taskId");
                            String isCompleted="not started";
                            Task task=new Task(taskDescription);
                            task.setIsCompleted(isCompleted);
                            task.setmaskTime(taskCreationTime);
                            task.setTaskId(taskId);
                            if (mUploadTaskListener != null) {
                                Log.d(TAG, "onResponse: "+task.getDescription());
                                mUploadTaskListener.onUploadTasksSucceed(task);
                            }

                        } catch (Exception e) {
                            if (mUploadTaskListener != null) {
                                mUploadTaskListener.onUploadTasksFailed(e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error network");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", getAuthKey());
                return authHeader;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        requestQueue.add(jsonObjReq);
    }

    public void updateTask(final Task task, final String description, final boolean isCompleted) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        final HashMap<String, Object> postParams = new HashMap<String, Object>();

        postParams.put("taskDescription", description==null?task.getDescription():description);
        Log.d(TAG, "updateTask: "+isCompleted);
        postParams.put("isCompleted",isCompleted);

        Log.d(TAG, "updateTask: "+"update");
        String taskId=task.getTaskId();
        Log.d(TAG, "updateTask: "+UPDATE_TASK+taskId);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                UPDATE_TASK+taskId, new JSONObject(postParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (mUpdateTaskListener != null) {
                                Log.d(TAG, "onResponse:"+task.getDescription());
                                mUpdateTaskListener.onUpdateTasksSucceed(task,description,isCompleted);
                            }

                        } catch (Exception e) {
                            if (mUpdateTaskListener != null) {
                                mUpdateTaskListener.onUpdateTasksFailed(e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error network");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", getAuthKey());
                return authHeader;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        requestQueue.add(jsonObjReq);
    }

    public void deleteTask(final Task taskToDelete){
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        final HashMap<String, Object> postParams = new HashMap<String, Object>();

        String id=taskToDelete.getTaskId();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                DELETE_TASK+id, new JSONObject(postParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: "+"task");
                            if (mDeleteTaskListener != null) {
                                mDeleteTaskListener.onDeleteTasksSucceed(taskToDelete);
                            }

                        } catch (Exception e) {
                            if (mDeleteTaskListener != null) {
                                mDeleteTaskListener.onDeleteTasksFailed();
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error network");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", getAuthKey());
                return authHeader;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        requestQueue.add(jsonObjReq);
    }

    public void setAuthKey(String authKey) { super.setAuthKey(authKey); }

    public String getAuthKey() { return super.getAuthKey(); }

    public void setTask(Task task) { super.setTask(task); }

    public Task getTask() {
        return super.getTask();
    }

    public void setTherapist(Therapist therapist) { super.setTherapist(therapist); }

    public Therapist getTherapist() {
        return super.getTherapist();
    }
}
