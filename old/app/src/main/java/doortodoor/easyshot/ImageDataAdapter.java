package doortodoor.easyshot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import doortodoor.easyshot.database.DataItem;
import doortodoor.easyshot.database.ImageDatabaseManager;
import io.realm.Realm;

/**
 * Created by noble on 2017-02-10.
 */

public class ImageDataAdapter extends BaseAdapter {
    private final Context mContext;
    private Realm realm;
    private final ImageDatabaseManager mManager;
    private final ArrayList<DataItem> mItems;

    public ImageDataAdapter(Context context) {
        mContext = context;
        mManager = new ImageDatabaseManager(context);
        mItems = mManager.getAll();
    }

    @Override
    public int getCount() {
        return (int) mItems.size();
    }

    @Override
    public DataItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
        if (convertView == null) {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.linkdata_item, parent, false);

            final DataItem data = getItem(position);

            // TextView에 현재 position의 문자열 추가
            TextView text = (TextView) convertView.findViewById(R.id.linkdata_textView);
            text.setText(data.getColumnName());

            // TextView에 현재 position의 문자열 추가
            TextView text_url = (TextView) convertView.findViewById(R.id.linkdata_textView_url);
            text_url.setText(data.getColumnUrl());


            // 버튼을 터치 했을 때 이벤트 발생
            Button btn = (Button) convertView.findViewById(R.id.linkdata_button_go);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 터치 시 해당 아이템 이름 출력
                    String url = data.getColumnUrl();
                    if (!url.startsWith("http")) {
                        url = "http://" + url;
                    }
                    final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                    mContext.startActivity(intent);
                }
            });

            // 리스트 아이템을 터치 했을 때 이벤트 발생
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 터치 시 해당 아이템 이름 출력
                    Toast.makeText(mContext, "리스트 클릭 : " + data.getId() + " " + data.getColumnUrl(), Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    File imgFile = new File(data.getLocation());
                    ImageView imageView = new ImageView(mContext);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = null;
                        if (imgFile != null) {
                            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        }
                        imageView.setImageBitmap(myBitmap);
                    }
                    builder.setView(imageView);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });

            // 리스트 아이템을 길게 터치 했을 떄 이벤트 발생
            convertView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    // 터치 시 해당 아이템 이름 출력
                    Toast.makeText(mContext, "리스트 로옹 클릭 : " + data.getId() + " " + data.getColumnUrl(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        return convertView;
    }
}
