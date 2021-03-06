package com.project.mafalda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.mafalda.interfaz.ComunicaFragments;
import com.project.mafalda.model.User;
import com.project.mafalda.utilidades.Utilidades;
import com.project.mafalda.vista.EncuestaFragment;
import com.project.mafalda.vista.LoginActivity;
import com.project.mafalda.vista.MenuFragment;
import com.project.mafalda.vista.SplashDespedida;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

public class MainActivity extends AppCompatActivity implements  ComunicaFragments{
    FragmentTransaction fragmentTransaction;
    MenuFragment menu;
    private boolean bandera = false;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;

    Utilidades uti = new Utilidades();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        mAuth = FirebaseAuth.getInstance();
    }

    /**LLAMAMOS AL METODO onStart() para que al iniciar verifiquemos si existe alguna cuenta
     * registrada**/

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser concurretUser = mAuth.getCurrentUser();
        if(concurretUser!=null){
            validar(concurretUser);
        }else{
            abrirLogin();
            Log.e("ERROR: ","Fallo en el concurrentUser de Firebase");
        }
    }

    private void validar(FirebaseUser result) {
            /**GUARDO EL NOMBRE USUARIO Y SU Uid*/
            User.getInstance().setUsuario(result.getDisplayName());
            User.getInstance().setID(result.getUid());

            Log.e("ID Cuenta: ",""+result.getUid());
            Log.e("ID User: ",User.getInstance().getID());

            /**GENERO EL TOKEN PARA PODER PASARLO AL HEADER DE LOS RESPONS*/
            String compactJws = Jwts.builder()
                    .setSubject(""+User.getInstance().getID())
                    //.signWith(SignatureAlgorithm.HS256, KEY)
                    .signWith(SignatureAlgorithm.HS256,TextCodec.BASE64URL.encode(uti.KEY))
                    .compact();
            Log.e("JWT OBTENIDO:",compactJws);

            User.getInstance().setTOKEN(compactJws);


            /**BANDERA PARA IDENTIFICAR SI ES PRIMERA VEZ QUE ENTRA. SIRVE PARA EL ONDESTROY*/
            bandera = true;

            /**BLOQUEO EL GIRO DE PANTALLA*/
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_main);

            /** INSTANCIO EL FRAGMENT MENU Y LO ASIGNO AL CONTENEDOR DEL MAIN PRINCIPAL**/
            menu = new MenuFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contenedor,menu).commit();
    }

    private void abrirLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void mostrarEncuesta(String nombre) {
        /** ENVIO DATOS ID Y PREGUNTA AL FRAGMENT DE LA ENCUESTA PARA OBTENER LOS OTROS DATOS
         (OPCIONES, IMAGENES)**/
        Bundle objEnviado = new Bundle();
        //objEnviado.putInt("id",id);
        objEnviado.putString("nombre",nombre);
        EncuestaFragment encuestaFragment = new EncuestaFragment();
        encuestaFragment.setArguments(objEnviado);
        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, encuestaFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    /**Metodo para el boton logOut**/

    public void logOut(View view) {
        Auth.GoogleSignInApi.signOut(googleSignInClient.asGoogleApiClient())
                .setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    bandera = false;
                    abrirLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "NO SE PUDO CERRAR SESI??N",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**CUANDO LA ACTIVIDAD PRINCIPAL SE DESTRUYE INICIO EL SPLASH DESPEDIDA**/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bandera == true){
            //Toast.makeText(this, "??GRACIAS POR PARTICIPAR!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, SplashDespedida.class);
            startActivity(intent);
            finish();
        }
    }

}
