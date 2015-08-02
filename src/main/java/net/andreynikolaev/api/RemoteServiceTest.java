package net.andreynikolaev.api;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author <a href="mailto:ich@andrey-nikolaev.net">Andrey Nikolaev</a>
 */
@Remote
public interface RemoteServiceTest extends Serializable{
 
    int add(int a, int b);
    
    List<TestObject> getlListObject();
}