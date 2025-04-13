package br.com.ikaro.atividadeavaliativa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.ikaro.atividadeavaliativa.models.Report;
import br.com.ikaro.atividadeavaliativa.models.ReportImage;
import br.com.ikaro.atividadeavaliativa.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "envirocrime.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_REPORTS = "reports";
    private static final String TABLE_IMAGES = "report_images";

    private static final String KEY_ID = "id";

    private static final String KEY_NAME = "name";
    private static final String KEY_CPF = "cpf";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_ADMIN = "is_admin";

    private static final String KEY_TITLE = "title";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ANONYMOUS = "anonymous";
    private static final String KEY_STATUS = "status";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_ADMIN_NOTES = "admin_notes";

    private static final String KEY_REPORT_ID = "report_id";
    private static final String KEY_IMAGE_PATH = "image_path";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_CPF + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_PHONE + " TEXT,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_IS_ADMIN + " INTEGER DEFAULT 0" + ")";

    private static final String CREATE_TABLE_REPORTS = "CREATE TABLE " + TABLE_REPORTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TITLE + " TEXT,"
            + KEY_TYPE + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_LOCATION + " TEXT,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_ANONYMOUS + " INTEGER DEFAULT 0,"
            + KEY_STATUS + " TEXT,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + KEY_ADMIN_NOTES + " TEXT" + ")";

    private static final String CREATE_TABLE_IMAGES = "CREATE TABLE " + TABLE_IMAGES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_REPORT_ID + " INTEGER,"
            + KEY_IMAGE_PATH + " TEXT,"
            + "FOREIGN KEY(" + KEY_REPORT_ID + ") REFERENCES " + TABLE_REPORTS + "(" + KEY_ID + ")" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_REPORTS);
        db.execSQL(CREATE_TABLE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String name, String cpf, String email, String phone, String password, boolean isAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);
        values.put(KEY_CPF, cpf);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PHONE, phone);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_IS_ADMIN, isAdmin ? 1 : 0);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {KEY_ID, KEY_NAME, KEY_CPF, KEY_EMAIL, KEY_PHONE, KEY_IS_ADMIN};
        String selection = KEY_EMAIL + " = ? AND " + KEY_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            String cpf = cursor.getString(cursor.getColumnIndex(KEY_CPF));
            String userEmail = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
            String phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE));
            boolean isAdmin = cursor.getInt(cursor.getColumnIndex(KEY_IS_ADMIN)) == 1;

            user = new User(id, name, cpf, userEmail, phone, isAdmin);
            cursor.close();
        }

        db.close();
        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {KEY_ID, KEY_NAME, KEY_CPF, KEY_EMAIL, KEY_PHONE, KEY_IS_ADMIN};
        String selection = KEY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            String cpf = cursor.getString(cursor.getColumnIndex(KEY_CPF));
            String email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
            String phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE));
            boolean isAdmin = cursor.getInt(cursor.getColumnIndex(KEY_IS_ADMIN)) == 1;

            user = new User(id, name, cpf, email, phone, isAdmin);
            cursor.close();
        }

        db.close();
        return user;
    }

    public boolean userExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = KEY_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return exists;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String cpf = cursor.getString(cursor.getColumnIndex(KEY_CPF));
                String email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
                String phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE));
                boolean isAdmin = cursor.getInt(cursor.getColumnIndex(KEY_IS_ADMIN)) == 1;

                User user = new User(id, name, cpf, email, phone, isAdmin);
                users.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return users;
    }

    public long addReport(String title, String type, String description, String location, int userId, boolean anonymous, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, title);
        values.put(KEY_TYPE, type);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_LOCATION, location);
        values.put(KEY_USER_ID, userId);
        values.put(KEY_ANONYMOUS, anonymous ? 1 : 0);
        values.put(KEY_STATUS, status);

        long reportId = db.insert(TABLE_REPORTS, null, values);
        db.close();

        return reportId;
    }

    public Report getReportById(int reportId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Report report = null;

        String[] columns = {KEY_ID, KEY_TITLE, KEY_TYPE, KEY_DESCRIPTION, KEY_LOCATION,
                KEY_USER_ID, KEY_ANONYMOUS, KEY_STATUS, KEY_CREATED_AT, KEY_ADMIN_NOTES};
        String selection = KEY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(reportId)};

        Cursor cursor = db.query(TABLE_REPORTS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            String type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
            String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
            String location = cursor.getString(cursor.getColumnIndex(KEY_LOCATION));
            int userId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
            boolean anonymous = cursor.getInt(cursor.getColumnIndex(KEY_ANONYMOUS)) == 1;
            String status = cursor.getString(cursor.getColumnIndex(KEY_STATUS));
            String createdAt = cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT));
            String adminNotes = cursor.getString(cursor.getColumnIndex(KEY_ADMIN_NOTES));

            report = new Report(id, title, type, description, location, userId, anonymous, status, createdAt, adminNotes);
            cursor.close();
        }

        db.close();
        return report;
    }

    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_REPORTS + " ORDER BY " + KEY_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                String type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
                String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
                String location = cursor.getString(cursor.getColumnIndex(KEY_LOCATION));
                int userId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
                boolean anonymous = cursor.getInt(cursor.getColumnIndex(KEY_ANONYMOUS)) == 1;
                String status = cursor.getString(cursor.getColumnIndex(KEY_STATUS));
                String createdAt = cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT));
                String adminNotes = cursor.getString(cursor.getColumnIndex(KEY_ADMIN_NOTES));

                Report report = new Report(id, title, type, description, location, userId, anonymous, status, createdAt, adminNotes);
                reports.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reports;
    }

    public List<Report> getUserReports(int userId) {
        List<Report> reports = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = KEY_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = KEY_CREATED_AT + " DESC";

        Cursor cursor = db.query(TABLE_REPORTS, null, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                String type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
                String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
                String location = cursor.getString(cursor.getColumnIndex(KEY_LOCATION));
                boolean anonymous = cursor.getInt(cursor.getColumnIndex(KEY_ANONYMOUS)) == 1;
                String status = cursor.getString(cursor.getColumnIndex(KEY_STATUS));
                String createdAt = cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT));
                String adminNotes = cursor.getString(cursor.getColumnIndex(KEY_ADMIN_NOTES));

                Report report = new Report(id, title, type, description, location, userId, anonymous, status, createdAt, adminNotes);
                reports.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reports;
    }

    public List<Report> getReportsByStatus(String status) {
        List<Report> reports = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = KEY_STATUS + " = ?";
        String[] selectionArgs = {status};
        String orderBy = KEY_CREATED_AT + " DESC";

        Cursor cursor = db.query(TABLE_REPORTS, null, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                String type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
                String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
                String location = cursor.getString(cursor.getColumnIndex(KEY_LOCATION));
                int userId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
                boolean anonymous = cursor.getInt(cursor.getColumnIndex(KEY_ANONYMOUS)) == 1;
                String reportStatus = cursor.getString(cursor.getColumnIndex(KEY_STATUS));
                String createdAt = cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT));
                String adminNotes = cursor.getString(cursor.getColumnIndex(KEY_ADMIN_NOTES));

                Report report = new Report(id, title, type, description, location, userId, anonymous, reportStatus, createdAt, adminNotes);
                reports.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reports;
    }

    public boolean updateReportStatus(int reportId, String status, String adminNotes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_STATUS, status);
        values.put(KEY_ADMIN_NOTES, adminNotes);

        int result = db.update(TABLE_REPORTS, values, KEY_ID + " = ?", new String[]{String.valueOf(reportId)});
        db.close();

        return result > 0;
    }

    public boolean addReportImage(long reportId, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_REPORT_ID, reportId);
        values.put(KEY_IMAGE_PATH, imagePath);

        long result = db.insert(TABLE_IMAGES, null, values);
        db.close();

        return result != -1;
    }

    public List<ReportImage> getReportImages(int reportId) {
        List<ReportImage> images = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = KEY_REPORT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(reportId)};

        Cursor cursor = db.query(TABLE_IMAGES, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String imagePath = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH));

                ReportImage image = new ReportImage(id, reportId, imagePath);
                images.add(image);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return images;
    }

    public Map<String, Integer> getReportCountsByStatus() {
        Map<String, Integer> statusCounts = new HashMap<>();

        statusCounts.put("Pendente", 0);
        statusCounts.put("Investigando", 0);
        statusCounts.put("Resolvido", 0);
        statusCounts.put("Rejeitado", 0);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_STATUS + ", COUNT(*) as count FROM " + TABLE_REPORTS + " GROUP BY " + KEY_STATUS, null);

        if (cursor.moveToFirst()) {
            do {
                String status = cursor.getString(0);
                int count = cursor.getInt(1);
                statusCounts.put(status, count);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return statusCounts;
    }

    public Map<String, Integer> getReportCountsByType() {
        Map<String, Integer> typeCounts = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_TYPE + ", COUNT(*) as count FROM " + TABLE_REPORTS + " GROUP BY " + KEY_TYPE, null);

        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(0);
                int count = cursor.getInt(1);
                typeCounts.put(type, count);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return typeCounts;
    }

    public int getTotalReportCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REPORTS, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public int getReportCountByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REPORTS + " WHERE " + KEY_STATUS + " = ?", new String[]{status});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public Cursor getReportsByType() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT type, COUNT(*) as count FROM reports GROUP BY type";
        return db.rawQuery(query, null);
    }
}
