package edu.uncc.assessment05;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import edu.uncc.assessment05.fragments.FavoritesFragment;
import edu.uncc.assessment05.fragments.NftsFragment;
import edu.uncc.assessment05.fragments.auth.LoginFragment;
import edu.uncc.assessment05.fragments.auth.RegisterFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener, NftsFragment.NftsListener {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mAuth.getCurrentUser() == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.rootView, new LoginFragment())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.rootView, new NftsFragment())
                    .commit();
        }
    }

    @Override
    public void goToRegister() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new RegisterFragment())
                .commit();
    }

    @Override
    public void goToLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void authCompleted() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new NftsFragment())
                .commit();
    }

    @Override
    public void logout() {
        mAuth.signOut();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void goToFavorite() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView,new FavoritesFragment())
                .commit();
    }

}