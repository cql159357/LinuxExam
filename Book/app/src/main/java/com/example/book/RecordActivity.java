package com.example.book;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.book.bean.BookBean;
import com.example.book.database.SQLiteHelper;
import com.example.book.utils.HttpUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView book_back;
    TextView book_name;
    EditText book_author;
    ImageView delete;
    ImageView book_save;
    SQLiteHelper mSQLiteHelper;
    TextView book;
    String id;
    RecordActivity.MHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        book_back = (ImageView) findViewById(R.id.book_back);
        book_name = (EditText) findViewById(R.id.book_name);
        book_author = (EditText) findViewById(R.id.book_author);
        delete = (ImageView) findViewById(R.id.delete);
        book_save = (ImageView) findViewById(R.id.book_save);
        book = (TextView) findViewById(R.id.book);
        book_back.setOnClickListener(this);
        delete.setOnClickListener(this);
        book_save.setOnClickListener(this);
        mHandler = new RecordActivity.MHandler();
        initData();
    }

    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(this);
        book.setText("添加记录");

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            if (id != null) {
                book.setText("修改记录");
                book_author.setText(intent.getStringExtra("author"));
                book_name.setText(intent.getStringExtra("name"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.book_back:// 后退按钮
            finish();
            break;
        case R.id.delete:// 清空按钮
            book_name.setText("");
            book_author.setText("");
            break;
        case R.id.book_save:
            // 获取输入内容
            String author = book_author.getText().toString().trim();
            String name = book_name.getText().toString().trim();
            BookBean bookBean = new BookBean();
            bookBean.setId(id);
            bookBean.setName(name);
            bookBean.setAuthor(author);

            // 向数据库中添加内容
            if (id != null) {
                if (author.length() > 0 && name.length() >0) {
                    httpUpdate(bookBean);
                } else {
                    showToast("修改内容不能为空");
                }
            } else { // 添加记录界面的保存操作
                // 向数据库中添加数据
                if (author.length() > 0 && name.length() >0) {
                    httpAdd(bookBean);
                } else {
                    showToast("作者书名不能为空");
                }
            }
            break;
        }
    }

    // 增加
    public void httpAdd(BookBean req) {
        httpAddOrUpdate("add", req);
    }

    // 更新
    public void httpUpdate(BookBean req) {
        httpAddOrUpdate("update", req);
    }

    private void httpAddOrUpdate(String action, BookBean req) {
        Call call = new OkHttpClient().newCall(HttpUtils.postRequestBuilder(action, req));

        // andriod不能使用同步调用
        // 开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 错误处理
                    showToast("更新失败");
                    return;
                }

                // 线程内不能直接操作主线程的view，需要借助MQ
                Message msg = new Message();
                msg.obj = response.body().string();

                switch (action) {
                    case "add":
                        msg.what = HttpUtils.MSG_CREATE_OK;
                        msg.obj = req;
                        break;
                    case "update":
                        msg.what = HttpUtils.MSG_UPDATE_OK;
                        msg.obj = req;
                        break;
                }

                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    public void showToast(String message) {
        Toast.makeText(RecordActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 事件捕获
     */
    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case HttpUtils.MSG_CREATE_OK:
                case HttpUtils.MSG_UPDATE_OK:
                    if (msg.obj != null) {
                        showToast("更新成功");
                        setResult(2);
                        finish();
                    }
                    break;
                default:
                break;
            }
        }
    }
}
