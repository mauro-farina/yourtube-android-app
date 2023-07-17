package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;

public class ChannelInfoRequest extends YouTubeApiRequest<String> {

    public ChannelInfoRequest(GoogleAccountCredential credential) {
        super(credential);
    }

    @Override
    public String call() throws Exception {
        YouTube.Channels.List channelRequest = youtubeService
                .channels()
                .list("snippet,contentDetails,statistics");

        ChannelListResponse response = channelRequest
                .setMine(true)
                .execute();
//        System.out.println(response);
        return response.getItems().get(0).getSnippet().getTitle();
    }
}