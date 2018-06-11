package com.example.rtyui.mvptalk.tool;

import android.accounts.Account;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.model.AccountModel;

import java.lang.reflect.Field;
import java.sql.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rtyui on 2018/5/1.
 */

public class MySqliteHelper {


    private SQLiteDatabase sqLiteDatabase = null;

    public static MySqliteHelper instance;

    public static MySqliteHelper getInstance() {
        if (instance == null)
            instance = new MySqliteHelper();
        return instance;
    }

    public MySqliteHelper(){
        sqLiteDatabase = App.context.openOrCreateDatabase("data.db",MODE_PRIVATE,null);
    }

    public void mkTable(Class c){
        Field[] fields = c.getFields();
        StringBuilder mktable = new StringBuilder("create table " +
                c.getSimpleName() + "_" + AccountModel.getInstance().currentUser.id +
                " (_id integer primary key autoincrement");
        for (Field field : fields){
            switch (field.getType().getName()){
                case "int":
                    mktable.append(',' + field.getName() + " integer");
                    break;
                case "java.lang.String":
                    mktable.append(',' + field.getName() + " text");
                    break;
                case "long":
                    mktable.append(',' + field.getName() + " long");
            }
        }
        mktable.append(')');
        try{
            sqLiteDatabase.execSQL(mktable.toString());
        }catch(Exception e){}
    }


    public void insert(Object object){
        Class c = object.getClass();
        Field[] fields = c.getFields();
        StringBuilder mktable = new StringBuilder("insert into " +
                c.getSimpleName() + "_" + AccountModel.getInstance().currentUser.id);

        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();


        try{
            for (Field field : fields){
                switch (field.getType().getName()){
                    case "int":
                    case "long":
                    case "java.lang.String":
                        name.append(field.getName() + ',');
                        if (field.getType().getName() == "java.lang.String")
                            value.append("'" + field.get(object).toString().replace("'", "''") + "',");
                        else
                            value.append("'" + field.get(object) + "',");
                        break;
                }
            }
        }catch(IllegalAccessException e){
        }

        System.out.println(name);
        name.deleteCharAt(name.length() - 1);
        value.deleteCharAt(value.length() - 1);
        mktable.append("(" + name + ")").append(" values(" + value + ")");
        sqLiteDatabase.execSQL(mktable.toString());
    }

    public List getAll(Class c){
        try{
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + c.getSimpleName() + "_" + AccountModel.getInstance().currentUser.id, null);
            return cursor2list(cursor, c);
        }catch(Exception e){
            mkTable(ChatBean.class);
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + c.getSimpleName() + "_" + AccountModel.getInstance().currentUser.id, null);
            return cursor2list(cursor, c);
        }
    }

    public void clear(Class c){
        sqLiteDatabase.execSQL("delete from " + c.getSimpleName() + "_" + AccountModel.getInstance().currentUser.id);
    }

    public void delete(Class c, String conditions){
        String string = "DELETE FROM " + c.getSimpleName() + "_" + AccountModel.getInstance().currentUser.id + " where ";
        string += conditions;
        System.out.println(string);
        sqLiteDatabase.execSQL(string);
    }

    public List query(Class c, String conditions){
        String string = "SELECT * from " + c.getSimpleName() + "_" + AccountModel.getInstance().currentUser.id + " where ";
        string += conditions;
        Cursor cursor = sqLiteDatabase.rawQuery(string, null);
        return cursor2list(cursor, c);
    }

    private List cursor2list(Cursor cursor, Class c){
        List<Object> list = new LinkedList<>();
        try {
            while (cursor.moveToNext()){
                Object o = c.newInstance();
                Field[] fields = c.getFields();
                for (int i = 1 ; i < cursor.getColumnCount(); i++){
                    switch (fields[i - 1].getType().getName()){
                        case "int":
                            fields[i - 1].setInt(o, cursor.getInt(i));
                            break;
                        case "java.lang.String":
                            fields[i - 1].set(o, cursor.getString(i));
                            break;
                        case "long":
                            fields[i - 1].set(o, cursor.getLong(i));
                            break;
                    }
                }
                list.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Class c, String results, String conditions){
        String string = "UPDATE " + c.getSimpleName() + "_" + AccountModel.getInstance().currentUser.id + " SET " + results +  " WHERE " + conditions;
        sqLiteDatabase.execSQL(string);
    }
}
