package com.cherish.myduban;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class BookList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.book_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

            final ListView listView = (ListView) rootView.findViewById(R.id.list);

            new AsyncTask<String, Void, JSONObject>() {
                @Override
                protected JSONObject doInBackground(String... strings) {
                    final String url = strings[0];
                    return readBookDataFromInternet(url);
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    super.onPostExecute(jsonObject);
                    listView.setAdapter(new MyAdapter(jsonObject));
                }
            }.execute("https://api.douban.com/v2/book/search?tag=%E8%AE%A1%E7%AE%97%E6%9C%BA");

            return rootView;
        }

        class MyAdapter extends BaseAdapter {

            private JSONObject jsonData;

            public MyAdapter(JSONObject jsonData) {
                this.jsonData = jsonData;
            }

            @Override
            public int getCount() {
                return jsonData.optJSONArray("books").length();
            }

            @Override
            public Object getItem(int position) {
                return jsonData.optJSONArray("books").opt(position);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {

                ViewHolder viewHolder;

                if (view == null) {
                    viewHolder = new ViewHolder();

                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    view = inflater.inflate(R.layout.list_item_book, viewGroup, false);

                    viewHolder.bookCover = (ImageView) view.findViewById(R.id.bookCover);
                    viewHolder.ratingBar = (RatingBar) view.findViewById(R.id.bookRating);
                    viewHolder.bookInformation = (TextView) view.findViewById(R.id.bookInformation);
                    viewHolder.bookName = (TextView) view.findViewById(R.id.bookName);

                    view.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }

                JSONObject data = (JSONObject) getItem(position);

                viewHolder.bookCover.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_cover));
                viewHolder.ratingBar.setRating((float) (data.optJSONObject("rating").optDouble("average") / 2));
                viewHolder.bookName.setText(data.optString("title"));
                viewHolder.bookInformation.setText(TextUtils.join("/", new String[]{
                                data.optJSONArray("author").optString(0),
                                data.optString("publisher"),
                                data.optString("pubdate")}
                ));

                return view;
            }
        }

        static class ViewHolder {
            ImageView bookCover;
            RatingBar ratingBar;
            TextView bookName;
            TextView bookInformation;
        }

        private JSONObject readBookData() {

            JSONObject jsonData = null;

            InputStream in = getActivity().getResources().openRawResource(R.raw.data);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            try {
                jsonData = new JSONObject(bufferedReader.readLine());

                Log.d("print json data", jsonData.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return jsonData;
        }

        private JSONObject readBookDataFromInternet(String urlString) {

            StringBuffer stringBuffer = new StringBuffer();
            JSONObject json = new JSONObject();
            String line;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                bufferedReader.close();
                json = new JSONObject(stringBuffer.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }
    }
}
