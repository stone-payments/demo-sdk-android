package br.com.stonesdk.sdkdemo.activities

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.stone.posandroid.net.client.api.NetClientPreRequest
import br.com.stone.posandroid.net.client.api.NetClientRequest
import br.com.stone.posandroid.net.client.api.NetClientResponse
import br.com.stone.posandroid.net.client.api.NetClientUrl
import br.com.stone.posandroid.net.client.api.NetworkingClient
import br.com.stone.posandroid.net.client.api.enumerators.EventType
import br.com.stone.posandroid.net.client.domain.model.RequestType
import br.com.stone.posandroid.net.client.domain.model.SchemeType
import br.com.stone.posandroid.net.phc_common.api.PhcServiceName
import java.util.concurrent.Executors

/**
 * Teste de conectividade do netclient (PRJHW-7286): pre-connect, Keep-Alive e reuso de socket.
 *
 * O pagamento do demo NAO passa pelo netclient (usa AdvancedHttp), entao esta tela chama o
 * NetworkingClient diretamente. O host real vem do PHC a partir do PhcServiceName escolhido.
 *
 * Como ler o resultado: cada passo mostra quantos eventos CONNECTION a chamada gerou.
 *  - connect()                     -> esperado 1 (abre o socket)
 *  - request() logo depois / < TTL -> esperado 0 (reusa o socket do pool)
 *  - request() depois do TTL       -> 1 (socket novo) se a conexao foi encerrada
 * Logcat: tag NetClientTest + eventos BaseEventListener. Sockets: adb shell ss -tn
 */
class NetClientTestActivity : AppCompatActivity() {

    private data class Preset(val label: String, val host: String, val service: PhcServiceName)

    private val presets = listOf(
        Preset("AKAMAI - payments-transaction", "payments-stg.stone.com.br", PhcServiceName.API_GATEWAY),
        Preset("Controle - PDM v3", "poidownloadmanager-stg.stone.com.br", PhcServiceName.PDM_V3),
    )

    private val executor = Executors.newSingleThreadExecutor()

    @Volatile
    private var cancelled = false

    private var client: NetworkingClient? = null

    private lateinit var presetSpinner: Spinner
    private lateinit var methodSpinner: Spinner
    private lateinit var hostEdit: EditText
    private lateinit var pathEdit: EditText
    private lateinit var waitsEdit: EditText
    private lateinit var keepAliveCheck: CheckBox
    private lateinit var logText: TextView
    private lateinit var scroll: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Teste netclient (PRJHW-7286)"
        // A bateria dura minutos; a tela nao pode apagar no meio.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(buildUi())
        applyPreset(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelled = true
        executor.shutdownNow()
        runCatching { client?.disconnect() }
    }

    private fun buildUi(): View {
        val pad = (12 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }

        // Acoes no topo (sempre visiveis); configuracao abaixo.
        root.addView(button("1. Connect (pre-connect)") { runAsync { step("connect()") { c -> c.connect(preRequest()) } } })
        root.addView(button("2. Request") { runAsync { step("request()") { c -> c.request(request()) } } })
        root.addView(button("3. Serie: 2 requests seguidos") { runAsync { series() } })
        root.addView(button("4. Bateria completa") { runAsync { battery() } })
        root.addView(button("5. Connect x2 (reuso)") { runAsync { connectTwice() } })
        root.addView(button("Disconnect (quebra o pool: reiniciar o app depois)") { runAsync { disconnect() } })
        root.addView(button("Cancelar bateria") { cancelled = true; log("-- cancelamento pedido") })
        root.addView(button("Limpar log") { logText.text = "" })

        presetSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@NetClientTestActivity,
                android.R.layout.simple_spinner_dropdown_item,
                presets.map { it.label },
            )
            onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) =
                    applyPreset(pos)

                override fun onNothingSelected(p: android.widget.AdapterView<*>?) = Unit
            }
        }
        root.addView(label("Perfil"))
        root.addView(presetSpinner)

        hostEdit = EditText(this).apply { setSingleLine(); hint = "host" }
        root.addView(label("Host"))
        root.addView(hostEdit)

        pathEdit = EditText(this).apply { setSingleLine(); setText("/") }
        root.addView(label("Path"))
        root.addView(pathEdit)

        methodSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@NetClientTestActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("HEAD", "GET"),
            )
        }
        root.addView(label("Metodo"))
        root.addView(methodSpinner)

        keepAliveCheck = CheckBox(this).apply {
            text = "Enviar Connection: Keep-Alive"
            isChecked = true
        }
        root.addView(keepAliveCheck)

        waitsEdit = EditText(this).apply { setSingleLine(); setText("2,20,31,40,60") }
        root.addView(label("Esperas da bateria (s)"))
        root.addView(waitsEdit)

        logText = TextView(this).apply {
            typeface = Typeface.MONOSPACE
            textSize = 11f
            setTextIsSelectable(true)
        }
        // Controles e log em areas separadas: o auto-scroll do log nao pode mover os botoes.
        val controlsScroll = ScrollView(this).apply { addView(root) }
        scroll = ScrollView(this).apply {
            setBackgroundColor(0xFFEEEEEE.toInt())
            addView(logText, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        val logHeight = (240 * resources.displayMetrics.density).toInt()
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(controlsScroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
            addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, logHeight))
        }
    }

    private fun label(text: String) = TextView(this).apply { this.text = text; setPadding(0, 16, 0, 0) }

    private fun button(text: String, onClick: () -> Unit) = Button(this).apply {
        this.text = text
        setOnClickListener { onClick() }
    }

    private fun applyPreset(position: Int) {
        hostEdit.setText(presets[position].host)
    }

    private fun selectedService(): PhcServiceName = presets[presetSpinner.selectedItemPosition].service

    private fun selectedMethod(): RequestType =
        if (methodSpinner.selectedItem == "GET") RequestType.GET else RequestType.HEAD

    private fun url(): NetClientUrl {
        val builder = NetClientUrl.Builder()
            .scheme(SchemeType.HTTPS)
            .host(hostEdit.text.toString().trim())
            .port(443)
        pathEdit.text.toString().split('/').filter { it.isNotBlank() }.forEach { builder.addPathSegment(it) }
        return builder.build()
    }

    private fun preRequest(): NetClientPreRequest {
        val builder = NetClientPreRequest.Builder()
            .url(url())
            .method(selectedMethod())
            .phcServiceName(selectedService())
            .addTag("net-client-test")
        if (keepAliveCheck.isChecked) builder.addHeader("Connection", "Keep-Alive")
        return builder.build()
    }

    private fun request(): NetClientRequest {
        val builder = NetClientRequest.Builder()
            .url(url())
            .method(selectedMethod())
            .phcServiceName(selectedService())
            .addTag("net-client-test")
        if (keepAliveCheck.isChecked) builder.addHeader("Connection", "Keep-Alive")
        return builder.build()
    }

    private fun client(): NetworkingClient =
        client ?: NetClientFactoryHelper.create(10_000L, 30_000L, 30_000L, 30_000L).also {
            client = it
            log("-- NetworkingClient criado (ConnectionType.DEFAULT, via NetClientFactoryHelper)")
        }

    private fun disconnect() {
        runCatching { client?.disconnect() }
            .onSuccess { log("-- disconnect() ok") }
            .onFailure { log("-- disconnect() falhou: $it") }
        client = null
    }

    /** Executa uma chamada e imprime status, protocolo e quantos eventos CONNECTION ela gerou. */
    private fun step(name: String, call: (NetworkingClient) -> NetClientResponse): Int? {
        val started = System.currentTimeMillis()
        return try {
            val response = call(client())
            val elapsed = System.currentTimeMillis() - started
            val connects = response.events.count { it.eventType == EventType.CONNECTION }
            val sends = response.events.count { it.eventType == EventType.SEND }
            val verdict = if (connects == 0) "REUSOU socket" else "ABRIU socket ($connects connect)"
            log("[$name] HTTP ${response.statusCode} ${response.protocol} | CONNECTION=$connects SEND=$sends | ${elapsed}ms | $verdict")
            response.events.forEach {
                log("    evento ${it.eventType} ${it.endTime - it.beginTime}ms erro=${it.error?.javaClass?.simpleName}")
            }
            connects
        } catch (t: Throwable) {
            log("[$name] FALHOU em ${System.currentTimeMillis() - started}ms: ${t.javaClass.simpleName}: ${t.message}")
            generateSequence(t.cause) { it.cause }.take(4).forEach { log("    causa: ${it.javaClass.name}: ${it.message}") }
            t.stackTrace.take(6).forEach { log("    at $it") }
            null
        }
    }

    private fun series() {
        log("== SERIE: 2 requests seguidos (host=${hostEdit.text}, ${selectedMethod()}) ==")
        step("request #1") { c -> c.request(request()) }
        step("request #2") { c -> c.request(request()) }
    }

    private fun battery() {
        cancelled = false
        val waits = waitsEdit.text.toString().split(',').mapNotNull { it.trim().toLongOrNull() }
        log("== BATERIA host=${hostEdit.text} servico=${selectedService()} ${selectedMethod()} " +
            "keep-alive=${keepAliveCheck.isChecked} esperas=$waits ==")
        // Cadeia continua, SEM disconnect(): no netclient 3.6.1.5 o pool continua devolvendo o motor ja encerrado
        // e toda chamada seguinte falha com "Parent job is Completed". Requests normais mantem o socket no pool do
        // OkHttp (connect()/pre-connect nao serve: cancela a propria chamada e o socket fecha na hora).
        // Cada request mede se o socket sobreviveu a ociosidade desde o request anterior:
        // CONNECTION=0 => socket reaproveitado; CONNECTION=1 => socket novo (o antigo expirou/foi fechado).
        step("request inicial") { c -> c.request(request()) } ?: return
        for (wait in waits) {
            if (cancelled) break
            log("-- ocioso por ${wait}s")
            sleepInterruptibly(wait)
            if (cancelled) break
            step("request apos ${wait}s ocioso") { c -> c.request(request()) }
        }
        log("== BATERIA FIM ==")
    }

    private fun connectTwice() {
        log("== CONNECT x2 (reuso do pre-connect), host=${hostEdit.text} ==")
        step("connect() #1") { c -> c.connect(preRequest()) }
        Thread.sleep(2_000)
        step("connect() #2 apos 2s") { c -> c.connect(preRequest()) }
    }

    private fun sleepInterruptibly(seconds: Long) {
        var left = seconds
        while (left > 0 && !cancelled) {
            Thread.sleep(1_000)
            left--
        }
    }

    private fun runAsync(block: () -> Unit) {
        executor.execute {
            try {
                block()
            } catch (t: Throwable) {
                log("ERRO: ${t.javaClass.simpleName}: ${t.message}")
            }
        }
    }

    private fun log(message: String) {
        Log.d(TAG, message)
        runOnUiThread {
            logText.append("$message\n")
            scroll.post { scroll.fullScroll(View.FOCUS_DOWN) }
        }
    }

    private companion object {
        const val TAG = "NetClientTest"
    }
}
