package cn.gavin.note.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import cn.gavin.note.R;
import cn.gavin.note.StringAdapter;

/**
 * Created by luoyuan on 2016/9/24.
 */
public class MainActivity extends Activity {
    private EditText editText;
    private String noteName;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        editText = (EditText) findViewById(R.id.editText);
        title = (TextView) findViewById(R.id.note_title);
        Button saveButton = (Button) findViewById(R.id.save_button);
        Button newNoteButton = (Button) findViewById(R.id.new_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNote();
            }
        });
       /* File root = new File(Environment.getExternalStorageDirectory() + "/littlenotes");
        if(root.exists()){
            root.delete();
        }*/
        File root = new File(Environment.getExternalStorageDirectory() + "/littlenotes/");
        if (!root.exists()) {
            root.mkdir();
        }
        findViewById(R.id.load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNote();
            }
        });
        findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareNoteIn();
            }
        });
        openNote();
    }

    public void save() {
        String text = editText.getText().toString();
        try {
            if(!text.trim().isEmpty()) {
                if(noteName == null){
                    noteName = buildEmptyName();
                }
                FileWriter writer = new FileWriter(Environment.getExternalStorageDirectory() + "/littlenotes/" + noteName, false);
                writer.write(text);
                writer.flush();
                writer.close();
                Toast.makeText(this, getString(R.string.save_tips), Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildEmptyName() {
        return new Date().toString().replaceAll(" ", "_").replaceAll(":", "_").replaceAll("\\+", "_");
    }

    public void setFileName(String fileName) {
        try {
            if (noteName != null) {
                save();
            }
            if (fileName != null) {
                this.noteName = fileName;
                loadNote(fileName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadNote(String noteName) {
        editText.setText("");
        title.setText(noteName);
        File file = new File(Environment.getExternalStorageDirectory() + "/littlenotes/" + noteName);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String s = reader.readLine();
                while (s != null) {
                    sb.append(s);
                    s = reader.readLine();
                    if(s!=null){
                        sb.append("\n");
                    }
                }
                editText.setText(sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void newNote() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        final EditText titleText = new EditText(this);
        dialog.setView(titleText);
        titleText.setHint("输入笔记名字");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.create_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String text = titleText.getText().toString();
                if (text.isEmpty()) {
                    text = buildEmptyName();
                }
                setFileName(text);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancle_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    protected void onDestroy() {
        save();
        super.onDestroy();
    }
    public void openNote() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        File root = new File(Environment.getExternalStorageDirectory() + "/littlenotes/");
        final StringAdapter<String> adapter = new StringAdapter<>(new ArrayList<>(Arrays.asList(root.list())));
        ListView listView = new ListView(this);
        listView.setAdapter(adapter);
        dialog.setView(listView);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file = v.getTag(R.string.item).toString();
                if (!file.isEmpty()) {
                    setFileName(file);
                }
                dialog.dismiss();
            }
        });
        adapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String file = v.getTag(R.string.item).toString();
                if (!file.isEmpty()) {
                    final AlertDialog deleteDialog = new AlertDialog.Builder(MainActivity.this).create();
                    deleteDialog.setMessage(getString(R.string.delete_tips) + file);
                    deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.conform_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteDialog.dismiss();
                            if (new File(Environment.getExternalStorageDirectory() + "/littlenotes/" + file).delete()) {
                                adapter.getData().remove(file);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancle_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteDialog.dismiss();
                        }
                    });
                   deleteDialog.show();

                }
                return false;
            }
        });
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.close_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (noteName == null && adapter.getData()!=null && adapter.getData().size() > 0) {
                    setFileName(adapter.getItem(0));
                }else if(noteName ==  null){
                    setFileName(new Date().toString().replaceAll(" ", "_").replaceAll(":", "_"));
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //分享文件
    public void shareSingleFile() {
        String imagePath = Environment.getExternalStorageDirectory() + "/littlenotes/" +  noteName;
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(imagePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("text/*");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    }
    //分享文本
    public void shareNoteIn() {
        String value = editText.getText().toString();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, value);
        shareIntent.setType("text/*");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    }

}
