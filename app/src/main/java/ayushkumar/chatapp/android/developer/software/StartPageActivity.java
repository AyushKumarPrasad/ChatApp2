package ayushkumar.chatapp.android.developer.software;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartPageActivity extends AppCompatActivity
{
    private Button new_account, already_have_account ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        new_account = (Button) findViewById(R.id.but1);
        already_have_account = (Button) findViewById(R.id.but2);

        new_account.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent ayush = new Intent(StartPageActivity.this, LoginActivity.class );
                startActivity(ayush);

            }
        });

        already_have_account.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent ayushkumar = new Intent(StartPageActivity.this, RegisterActivity.class );
                startActivity(ayushkumar);

            }
        });
    }
}
