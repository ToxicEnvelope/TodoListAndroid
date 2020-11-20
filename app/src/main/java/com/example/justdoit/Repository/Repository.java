package com.example.justdoit.Repository;

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
import com.example.justdoit.Model.Task;
import com.example.justdoit.Model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {

    private static final String TAG = "Repository";

    private final String SIGN_UP_URI = "http://10.0.0.11:8081/api/v1/users/sign-in";
    private final String LOGIN_URI = "http://10.0.0.11:8081/api/v1/users/login";
    private final String GET_TASK = "http://10.0.0.11:8081/api/v1/tasks/<task_id>";
    private final String GET_ALL_TASKS = "http://10.0.0.11:8081/api/v1/tasks";
    private final String UPLOAD_TASK = "http://10.0.0.11:8081/api/v1/tasks";
    private final String UPDATE_TASK = "http://10.0.0.11:8081/api/v1/tasks/";
    private final String DELETE_TASK = "http://10.0.0.11:8081/api/v1/tasks/";


    private static Repository mRepository;
    private User mUser;
    private Gson gson;
    private Context mContext;
    private String authKey;
    private Task mTask;


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



    public interface RepositorySignUpInterface {
        void onUserSignUpSucceed();
        void onUserSignUpSFailed();
    }

    private RepositorySignUpInterface mSignUpListener;

    public void setSignUpListener(RepositorySignUpInterface repositoryLoginInterface) {
        this.mSignUpListener = repositoryLoginInterface;
    }

    public interface RepositoryLoginInterface {
        void onUserLoginucceed();

        void onUserLoginSFailed();
    }

    private RepositoryLoginInterface mLoginListener;

    public void setLoginListener(RepositoryLoginInterface repositoryLoginInterface) {
        this.mLoginListener = repositoryLoginInterface;
    }


    public interface RepositoryDownloadTasksInterface {
        void onUserDownloadTasksucceed(List<Task> taskList);
        void onUserDownloadTasksSFailed(String error);
    }

    private RepositoryDownloadTasksInterface mDownloadTasksListener;

    public void setDownloadTasksListener(RepositoryDownloadTasksInterface repositoryDownloadTasksInterface) {
        this.mDownloadTasksListener = repositoryDownloadTasksInterface;
    }


    //***--------------**//
    //Upload Task
    public interface RepositoryUploadTaskInterface {
        void onUploadTasksucceed(Task task);
        void onUploadTasksSFailed(String error);
    }

    private RepositoryUploadTaskInterface mUploadTaskListener;

    public void setUploadTaskListener(RepositoryUploadTaskInterface repositoryUploadInterface) {
        this.mUploadTaskListener = repositoryUploadInterface;
    }

    //***--------------**//
    //Update Task

    public interface RepositoryUpdateTaskInterface {
        void onUpdateTasksucceed(Task task,String description,boolean isCompleted);
        void onUpdateTasksFailed(String error);
    }

    private RepositoryUpdateTaskInterface mUpdateTaskListener;

    public void setUpdateTaskListener(RepositoryUpdateTaskInterface repositoryUpdateInterface) {
        this.mUpdateTaskListener = repositoryUpdateInterface;
    }




    //***--------------**//
    //Delete Task


    public interface RepositoryDeleteTaskInterface {
        void onDeleteTasksucceed(Task task);
        void onDeleteTasksSFailed();
    }

    private RepositoryDeleteTaskInterface mDeleteTaskListener;

    public void setDeleteTaskListener(RepositoryDeleteTaskInterface repositoryDeleteInterface) {
        this.mDeleteTaskListener = repositoryDeleteInterface;
    }


    public void loginUser(final String email, final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        final HashMap<String, String> postParams = new HashMap<String, String>();

        postParams.put("email", email);
        postParams.put("password", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                LOGIN_URI, new JSONObject(postParams),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("TAG", response.toString());
                        try {
                            //Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_LONG).show();
                            if (mLoginListener != null) {
                                mLoginListener.onUserLoginucceed();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("TAG", "Error: " + error.getMessage());
                Log.d(TAG, "onErrorResponse: error network");
                if (mLoginListener != null) {
                    mLoginListener.onUserLoginSFailed();
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

                    authKey = jsonResponse.getJSONObject("headers").getString("Authorization");
                    Log.d(TAG, "parseNetworkResponse: " + authKey);
//                    mUser.setHeader(auth);
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

    public void signUpUser(User user) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        mUser = user;
        final HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("name", user.getName());
        postParams.put("email", user.getEmail());
        postParams.put("password", user.getPassword());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                SIGN_UP_URI, new JSONObject(postParams),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("TAG", response.toString());
                        try {
                            //Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_LONG).show();
                            Toast.makeText(mContext, "Thank you for your Sign UP", Toast.LENGTH_LONG).show();
                            if (mSignUpListener != null) {
                                mSignUpListener.onUserSignUpSucceed();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("TAG", "Error: " + error.getMessage());
                if (mSignUpListener != null) {
                    mSignUpListener.onUserSignUpSFailed();
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
                    authKey = jsonResponse.getJSONObject("headers").getString("Authorization");
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
                new com.android.volley.Response.Listener<JSONObject>() {
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
                                mDownloadTasksListener.onUserDownloadTasksucceed(tasksList);
                            }

                        } catch (Exception e) {
                            if (mDownloadTasksListener != null) {
                                mDownloadTasksListener.onUserDownloadTasksSFailed(e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error network");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", authKey);
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
                new com.android.volley.Response.Listener<JSONObject>() {
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
                                mUploadTaskListener.onUploadTasksucceed(task);
                            }

                        } catch (Exception e) {
                            if (mUploadTaskListener != null) {
                                mUploadTaskListener.onUploadTasksSFailed(e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error network");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", authKey);
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
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (mUpdateTaskListener != null) {
                                Log.d(TAG, "onResponse:"+task.getDescription());
                                mUpdateTaskListener.onUpdateTasksucceed(task,description,isCompleted);
                            }

                        } catch (Exception e) {
                            if (mUpdateTaskListener != null) {
                                mUpdateTaskListener.onUpdateTasksFailed(e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error network");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", authKey);
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
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: "+"task");
                            if (mDeleteTaskListener != null) {
                                mDeleteTaskListener.onDeleteTasksucceed(taskToDelete);
                            }

                        } catch (Exception e) {
                            if (mDeleteTaskListener != null) {
                                mDeleteTaskListener.onDeleteTasksSFailed();
                            }
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error network");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", authKey);
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

    public String getAuthKey() {
        return authKey;
    }


    public void setTask(Task task) {
        this.mTask = task;
    }

    public Task getTask() {
        return mTask;
    }
}
