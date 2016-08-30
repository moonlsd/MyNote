package net.awpspace.mynote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.awpspace.mynote.model.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<Note> mNotes = new ArrayList<>();
    private NoteAdapter mNoteAdapter;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("MyNote");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        initRecyclerview();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mNotes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mNotes.add(snapshot.getValue(Note.class));
                }
                mNoteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerview() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNoteAdapter = new NoteAdapter();
        mRecyclerView.setAdapter(mNoteAdapter);
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int itemPosition = mRecyclerView.getChildLayoutPosition(v);
            new MaterialDialog.Builder(MainActivity.this)
                    .title("Delete note")
                    .content("Are you sure to delete this note?")
                    .negativeText("Delete")
                    .positiveText("Cancel")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mNotes.remove(itemPosition);
                            myRef.setValue(mNotes);
                            mNoteAdapter.notifyDataSetChanged();
                        }
                    }).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            return true;
        } else if (item.getItemId() == R.id.menu_add_note) {
            addNewNote();
            return true;
        }
        return false;
    }

    private void addNewNote() {
        new MaterialDialog.Builder(this)
                .title("New Note")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input != null) {
                            Note newNote = new Note(input.toString());
                            mNotes.add(newNote);
                            myRef.setValue(mNotes);
                        }
                    }
                }).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private boolean checkLoggedIn() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return false;
        } else {
            return true;
        }
    }

    // Viewholder and adapter for recyclerview
    public static class NoteItemViewHolder extends RecyclerView.ViewHolder {

        TextView mTvContent;

        public NoteItemViewHolder(View itemView) {
            super(itemView);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    private class NoteAdapter extends RecyclerView.Adapter<NoteItemViewHolder> {

        @Override
        public NoteItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note_item, parent, false);
            view.setOnClickListener(clickListener);
            return new NoteItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteItemViewHolder holder, int position) {
            Note note = mNotes.get(position);
            holder.mTvContent.setText(note.getContent());
        }

        @Override
        public int getItemCount() {
            return mNotes == null ? 0 : mNotes.size();
        }
    }
}
