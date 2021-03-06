package mobi.liaison.loaders;

import android.app.Instrumentation;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ProviderTest {

    public Context mContext;
    public MockDatabaseHelper mMockDatabaseHelper;

    @Before
    public void setup(){
        mContext = new Instrumentation().getTargetContext();
        mMockDatabaseHelper = new MockDatabaseHelper(mContext, "DB_NAME", 1);
    }

    @Test
    public void queryingViewModel_callsQueryOnCorrectViewModel() {
        final Content mock = mock(Content.class);
        mMockDatabaseHelper.setContent(Lists.newArrayList((Content) mock));
        final Path path = new Path("PATH1", "PATH2");
        final List<Path> paths = Lists.newArrayList(path);
        when(mock.getPaths(any(Context.class))).thenReturn(paths);
        MockProvider mockProvider = new MockProvider(mMockDatabaseHelper);
        mockProvider.onCreate();
        mockProvider.query(Uri.parse("http://authority.com/PATH1/PATH2"), null, null, null, null);
        verify(mock).query(any(Context.class), any(SQLiteDatabase.class), eq(path), any(Uri.class), eq((String[]) null), eq((String) null), eq((String[]) null), eq((String) null));
    }

    @Test
    public void queryingViewModelWithValues_callsQueryOnCorrectViewModel() {
        final Content mock = mock(Content.class);
        mMockDatabaseHelper.setContent(Lists.newArrayList((Content) mock));
        final Path path = new Path("PATH1", "#", "PATH2");
        final List<Path> paths = Lists.newArrayList(path);
        when(mock.getPaths(any(Context.class))).thenReturn(paths);
        MockProvider mockProvider = new MockProvider(mMockDatabaseHelper);
        mockProvider.onCreate();
        mockProvider.query(Uri.parse("http://authority.com/PATH1/1/PATH2"), null, null, null, null);
        verify(mock).query(any(Context.class), any(SQLiteDatabase.class), eq(path), any(Uri.class), eq((String[]) null), eq((String) null), eq((String[]) null), eq((String) null));
    }

    public static class MockDatabaseHelper extends DatabaseHelper {

        private List<Content> mContent;

        public MockDatabaseHelper(Context context, String name, int version) {
            super(context, name, version);
        }

        public void setContent(final List<Content> content) {
            mContent = content;
        }

        @Override
        public List<Content> getContent(Context context) {
            return mContent;
        }


    }

    public static class MockProvider extends Provider {

        private final DatabaseHelper mDatabaseHelper;

        public MockProvider(final DatabaseHelper databaseHelper) {
            mDatabaseHelper = databaseHelper;
        }

        @Override
        public String getAuthority(Context context) {
            return "authority.com";
        }

        @Override
        protected DatabaseHelper onCreateDatabaseHelper(final Context context) {
            return mDatabaseHelper;
        }

        @Override
        protected SQLiteDatabase getSQLiteDatabase(Context context) {
            return null;
        }
    }
}