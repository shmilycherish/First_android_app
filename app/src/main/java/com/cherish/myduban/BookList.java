package com.cherish.myduban;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.BitmapFactory;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


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


        private JSONObject jsonData;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

            jsonData = readBookData();

            final ListView listView = (ListView) rootView.findViewById(R.id.list);

            listView.setAdapter(new BaseAdapter() {
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
                    View listView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_book, viewGroup, false);

                    JSONObject data = (JSONObject) getItem(position);

                    ImageView bookCover = (ImageView) listView.findViewById(R.id.bookCover);
                    RatingBar ratingBar = (RatingBar) listView.findViewById(R.id.bookRating);
                    TextView bookInformation = (TextView) listView.findViewById(R.id.bookInformation);
                    TextView bookName = (TextView) listView.findViewById(R.id.bookName);

                    bookCover.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_cover));
                    ratingBar.setRating((float) (data.optJSONObject("rating").optDouble("average") / 2));
                    bookName.setText(data.optString("title"));
                    bookInformation.setText(TextUtils.join("/", new String[]{
                                    data.optJSONArray("author").optString(0),
                                    data.optString("publisher"),
                                    data.optString("pubdate")}
                    ));

                    return listView;
                }
            });
            return rootView;
        }

        private JSONObject readBookData() {

            JSONObject jsonData = null;

            InputStream in = getActivity().getResources().openRawResource(R.raw.data);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            try {

                jsonData = new JSONObject(bufferedReader.readLine());
                bufferedReader.close();
                in.close();

                Log.d("print json data", jsonData.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonData;
        }
    }
}
