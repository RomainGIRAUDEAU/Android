package ihm.si3.polytech.projetnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class NewsDBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "mishap.db";
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    public NewsDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        SQLiteDatabase db = this.getWritableDatabase();

    }

    public void openDataBase() {
        //Open the database
        String myPath = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public void createDataBase() {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                // Copy the database in assets to the application database.
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database", e);
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database doesn't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS mishap");

        final String SQL_CREATE_MISHAP_TABLE = "CREATE TABLE mishap (\n" +
                "\tid INT PRIMARY KEY AUTOINCREMENT,\n" +
                "\tname VARCHAR(20) NOT NULL,\n" +
                "\tstate VARCHAR(20) NOT NULL CHECK (state IN ('TODO','INPROGRESS','DONE')),\n" +
                "\tpriority VARCHAR(20) NOT NULL CHECK (priority IN ('HIGH','LOW','MEDIUM','MAJOR','CRITICAL')),\n" +
                "\tdateStart DATE NOT NULL,\n" +
                "\tdateEnd DATE NOT NULL,\n" +
                "\tdescription TEXT,\n" +
                "\tidDeclarant INT NOT NULL, \n" +
                "\tplace VARCHAR(20) NOT NULL,\n" +
                "\ttag CHAR (10) NOT NULL\n" + ");";
        db.execSQL(SQL_CREATE_MISHAP_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mishap");
        onCreate(db);
    }

    public List<Mishap> getAllArticles() {

        return null;
    }

    public void addMishap(Mishap mishap) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO mishap (name,state,priority,dateStart,dateEnd,description,idDeclarant,place,tag) VALUES('salut mamene','TODO','HIGH','08.11.1997','08.11.1997','probleme dans la douche+" +
                "fererr',1,'Maison','test')");
    }
}