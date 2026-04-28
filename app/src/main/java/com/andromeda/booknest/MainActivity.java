package com.andromeda.booknest;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.andromeda.booknest.ui.favorites.FavoritesFragment;
import com.andromeda.booknest.ui.notes.NotesFragment;
import com.andromeda.booknest.ui.reading.ReadingListFragment;
import com.andromeda.booknest.ui.search.SearchFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        if(savedInstanceState==null){
            loadFragment(new SearchFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if(id == R.id.nav_search) fragment = new SearchFragment();
            else if(id == R.id.nav_favorites) fragment = new FavoritesFragment();
            else if(id == R.id.nav_reading) fragment = new ReadingListFragment();
            else if (id == R.id.nav_notes) fragment = new NotesFragment();
            else return false;
            loadFragment(fragment);
            return true;
        });
        }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}