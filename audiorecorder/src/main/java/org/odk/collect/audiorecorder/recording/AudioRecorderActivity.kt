package org.odk.collect.audiorecorder.recording

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_audio_recorder.done
import org.odk.collect.audiorecorder.R
import org.odk.collect.audiorecorder.getComponent
import org.odk.collect.audiorecorder.recorder.Recorder
import javax.inject.Inject

class AudioRecorderActivity : AppCompatActivity() {

    @Inject
    internal lateinit var recorder: Recorder

    private val viewModel: AudioRecorderViewModel by viewModels { AudioRecorderViewModel.Factory(recorder) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getComponent().inject(this)

        setTheme(intent.getIntExtra(ARGS.THEME, R.style.Theme_MaterialComponents_Light_NoActionBar))
        setContentView(R.layout.activity_audio_recorder)

        viewModel.recording.observe(this) { file ->
            if (file != null) {
                setResult(
                    Activity.RESULT_OK,
                    Intent().also {
                        it.data = Uri.parse(file.absolutePath)
                    }
                )

                finish()
            }
        }

        viewModel.start()

        done.setOnClickListener {
            viewModel.stop()
        }
    }

    object ARGS {
        const val THEME = "theme"
    }
}
