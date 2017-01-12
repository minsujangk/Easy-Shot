package doortodoor.easyshot.over_lollipop.assistant;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AssistSessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(Bundle args) {
        return (new AssistSession(this));

    }
}
