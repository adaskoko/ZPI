
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.User;
import com.example.zpi.models.Message;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import java.util.List;
import com.example.zpi.repositories.UserDao;
import com.example.zpi.repositories.MessageDao;

import java.sql.SQLException;

public class MessageDaoTest  {

    ConnectionSource connectionSource;

    @Before
    public void initialize(){
        connectionSource = BaseConnection.getConnectionSource();
    }

    @Test
    public void getConvos(){
        try{
            User u=new UserDao(connectionSource).queryForEq("ID", 24).get(0);
            List<User> result=new MessageDao(connectionSource).getConvosForUser(u);
            Assert.assertEquals(2, result.size());

        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}