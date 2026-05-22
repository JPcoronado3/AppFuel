package co.edu.unipiloto.scrumbacklog.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.cliente.SubsidioAprobadoActivity;

public class SubsidioService extends IntentService {

    public static final String CHANNEL_ID =
            "canal_subsidios";

    public SubsidioService() {
        super("SubsidioService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            // SIMULA PROCESAMIENTO
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String codigo =
                intent.getStringExtra("codigo");

        boolean aprobado =
                codigo.equals("1234AAAA");

        mostrarNotificacion(
                aprobado,
                codigo
        );
    }

    private void mostrarNotificacion(
            boolean aprobado,
            String codigo
    ) {

        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Canal Subsidios",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            notificationManager
                    .createNotificationChannel(channel);
        }

        Intent intent;

        String titulo;
        String mensaje;

        // SUBSIDIO APROBADO
        if (aprobado) {

            titulo = "Subsidio aprobado";

            mensaje =
                    "Usa el mismo código para reclamarlo";

            intent =
                    new Intent(
                            this,
                            SubsidioAprobadoActivity.class
                    );

            intent.putExtra("codigo", codigo);

        } else {

            titulo = "Subsidio no aprobado";

            mensaje = "Código inválido";

            intent =
                    new Intent();
        }

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                R.drawable.ic_launcher_foreground
                        )
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        notificationManager.notify(
                1,
                builder.build()
        );
    }
}