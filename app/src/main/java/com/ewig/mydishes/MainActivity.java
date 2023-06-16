package com.ewig.mydishes;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ewig.mydishes.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ArrayList<Dish> dishArrayList;
    DishAdapter dishAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        dishArrayList=new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dishAdapter = new DishAdapter(dishArrayList);
        binding.recyclerView.setAdapter(dishAdapter);
        getData();
        }

     private void getData(){
        try {
            SQLiteDatabase sqLiteDatabase= this.openOrCreateDatabase("Dishes",MODE_PRIVATE,null);
            Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM dishes",null);
            int nameIx = cursor.getColumnIndex("foodname");
            int idIx=cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                String name=cursor.getString(nameIx);
                int id=cursor.getInt(idIx);
                Dish dish =new Dish(name,id);
                dishArrayList.add(dish);
            }
            dishAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater= getMenuInflater();
    menuInflater.inflate(R.menu.dish_menu, menu);

    return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.addDish){
            Intent intent= new Intent(this, DishesActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}