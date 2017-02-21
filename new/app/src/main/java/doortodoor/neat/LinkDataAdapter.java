package doortodoor.neat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

/**
 * Created by noble on 2017-02-10.
 */

/*
* LinkDataAdapter extends BaseAdapter
* LinkData를 Realm에서 불러와서 ListView를 만들 때 이용되는 Adapter
* */

public class LinkDataAdapter extends BaseAdapter {
    private final Context mContext;
    private Realm realm;

    public LinkDataAdapter(Context context) {
        mContext = context;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public int getCount() {
        return (int) realm.where(LinkData.class).count();
    }

    @Override
    public LinkData getItem(int position) {
        return realm.where(LinkData.class).equalTo("id", position).findFirst();
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

            final LinkData linkData = getItem(position);

            // TextView에 현재 position의 문자열 추가
            TextView text = (TextView) convertView.findViewById(R.id.linkdata_textView);
            text.setText(linkData.getUrl());

            // 버튼을 터치 했을 때 이벤트 발생
            Button btn = (Button) convertView.findViewById(R.id.linkdata_button_go);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 터치 시 해당 아이템 이름 출력
                    final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(linkData.getUrl()));
                    mContext.startActivity(intent);
                }
            });

            // 리스트 아이템을 터치 했을 때 이벤트 발생
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 터치 시 해당 아이템 이름 출력
                    Toast.makeText(mContext, "리스트 클릭 : " + linkData.getId() + " " + linkData.getUrl(), Toast.LENGTH_SHORT).show();
                }
            });

            // 리스트 아이템을 길게 터치 했을 떄 이벤트 발생
            convertView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    // 터치 시 해당 아이템 이름 출력
                    Toast.makeText(mContext, "리스트 로옹 클릭 : " + linkData.getId() + " " + linkData.getUrl(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        return convertView;
    }
}
