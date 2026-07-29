package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by koros on 4/19/16.
 */
public class DetailsRepository extends DrishtiRepository {

    private static final String SQL =
            "CREATE virtual table ec_details using fts4 " + "" + "(base_entity_id"
                    + " VARCHAR, key VARCHAR, value VARCHAR, event_date datetime)";
    private static final String TABLE_NAME = "ec_details";
    private static final String BASE_ENTITY_ID_COLUMN = "base_entity_id";
    private static final String KEY_COLUMN = "key";
    private static final String VALUE_COLUMN = "value";
    private static final String EVENT_DATE_COLUMN = "event_date";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL);
    }

    public void add(String baseEntityId, String key, String value, Long timestamp) {
        addAll(baseEntityId, Collections.singletonMap(key, value), timestamp);
    }

    /**
     * Save a batch of key/value details for a client in a single query instead of one
     * SELECT + one INSERT/UPDATE per key, which was previously the main cost of updating
     * the details table for an event with several obs/address/attribute fields.
     */
    public void addAll(String baseEntityId, Map<String, String> values, Long timestamp) {
        if (values == null || values.isEmpty()) {
            return;
        }

        SQLiteDatabase database = masterRepository().getWritableDatabase();
        Map<String, String> existingValues = getAllDetailsForClient(baseEntityId);

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (existingValues.containsKey(key)) {
                if (value != null && value.equals(existingValues.get(key))) {
                    // Value has not changed, no need to update
                    continue;
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put(BASE_ENTITY_ID_COLUMN, baseEntityId);
                contentValues.put(KEY_COLUMN, key);
                contentValues.put(VALUE_COLUMN, value);
                contentValues.put(EVENT_DATE_COLUMN, timestamp);

                database.update(TABLE_NAME, contentValues,
                        BASE_ENTITY_ID_COLUMN + " = ? AND " + KEY_COLUMN + " MATCH ? ",
                        new String[]{baseEntityId, key});
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(BASE_ENTITY_ID_COLUMN, baseEntityId);
                contentValues.put(KEY_COLUMN, key);
                contentValues.put(VALUE_COLUMN, value);
                contentValues.put(EVENT_DATE_COLUMN, timestamp);

                database.insert(TABLE_NAME, null, contentValues);
            }
        }
    }

    public Map<String, String> getAllDetailsForClient(String baseEntityId) {
        Cursor cursor = null;
        Map<String, String> clientDetails = new HashMap<String, String>();
        try {
            SQLiteDatabase db = masterRepository().getReadableDatabase();
            String query =
                    "SELECT * FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID_COLUMN + " =?";
            cursor = db.rawQuery(query, new String[]{baseEntityId});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String key = cursor.getString(cursor.getColumnIndex(KEY_COLUMN));
                    String value = cursor.getString(cursor.getColumnIndex(VALUE_COLUMN));
                    clientDetails.put(key, value);
                } while (cursor.moveToNext());
            }
            return clientDetails;
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return clientDetails;
    }

    public Map<String, String> updateDetails(CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> details = getAllDetailsForClient(commonPersonObjectClient.entityId());
        details.putAll(commonPersonObjectClient.getColumnmaps());

        if (commonPersonObjectClient.getDetails() != null) {
            commonPersonObjectClient.getDetails().putAll(details);
        } else {
            commonPersonObjectClient.setDetails(details);
        }
        return details;
    }

    public Map<String, String> updateDetails(CommonPersonObject commonPersonObject) {
        Map<String, String> details = getAllDetailsForClient(commonPersonObject.getCaseId());
        details.putAll(commonPersonObject.getColumnmaps());

        if (commonPersonObject.getDetails() != null) {
            commonPersonObject.getDetails().putAll(details);
        } else {
            commonPersonObject.setDetails(details);
        }
        return details;
    }

    public boolean deleteDetails(String baseEntityId) {
        try {
            SQLiteDatabase db = masterRepository().getWritableDatabase();
            int afftectedRows = db
                    .delete(TABLE_NAME, BASE_ENTITY_ID_COLUMN + " = ?", new String[]{baseEntityId});
            if (afftectedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return false;
    }

}
