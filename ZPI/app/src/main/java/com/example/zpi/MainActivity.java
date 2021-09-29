package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetExample().execute();
    }

    private class SaveExample extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            String databaseUrl = "jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11439521";
            try {
                ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl, "sql11439521", "wdsA5B6LFC");
                Dao<User, String> userDao = DaoManager.createDao(connectionSource, User.class);

                User user = new User();
                user.setEmail("test2@test.com");
                user.setBirthDate(new Date());
                user.setJoiningDate(new Date());
                user.setName("test name");
                user.setPassword("test password");
                user.setSurname("test surname");

                Dao<ProductToTake, String> productDao = DaoManager.createDao(connectionSource, ProductToTake.class);
                ProductToTake product = new ProductToTake();
                product.setUser(user);
                product.setAmmount(3);
                product.setName("testowy produkt");

                userDao.create(user);
                productDao.create(product);

                connectionSource.close();

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }
    }

    private class GetExample extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            String databaseUrl = "jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11439521";
            try {
                ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl, "sql11439521", "wdsA5B6LFC");

                Dao<ProductToTake, String> productDao = DaoManager.createDao(connectionSource, ProductToTake.class);

                QueryBuilder<ProductToTake, String> queryBuilder = productDao.queryBuilder();
                queryBuilder.where().eq("Name", "testowy produkt");
                List<ProductToTake> products = queryBuilder.query();
                ProductToTake product = products.get(0);

                Dao<User, String> userDao = DaoManager.createDao(connectionSource, User.class);
                User user = product.getUser();
                userDao.refresh(user);

                Log.i("product", product.getName() + ' ' + product.getAmmount());
                Log.i("user", user.getEmail() + ' ' + user.getName());

                connectionSource.close();

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }
    }
}