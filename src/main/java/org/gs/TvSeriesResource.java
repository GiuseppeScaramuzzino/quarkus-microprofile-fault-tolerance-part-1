package org.gs;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.gs.model.Episode;
import org.gs.model.TvSeries;
import org.gs.proxy.EpisodeProxy;
import org.gs.proxy.TvSeriesProxy;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/tvseries")
public class TvSeriesResource {

  @RestClient TvSeriesProxy proxy;
  @RestClient
  EpisodeProxy episodeProxy;

  private List<TvSeries> tvSeriesList = new ArrayList<>();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@QueryParam("title") String title) {
    TvSeries tvSeries = getTvSeries(title);
    List<Episode> episodes = getEpisodes(tvSeries.getId());
    tvSeriesList.add(tvSeries);
    return Response.ok(episodes).build();
  }

  @Fallback(fallbackMethod = "fallbackGetEpisodes")
  public List<Episode> getEpisodes(Long id) {
    return episodeProxy.get(id);
  }

  private List<Episode> fallbackGetEpisodes(Long id) {
    return new ArrayList<>();
  }

  @Fallback(fallbackMethod = "fallbackGetTvSeries")
  public TvSeries getTvSeries(String title) {
    return proxy.get(title);
  }

  private TvSeries fallbackGetTvSeries(String title) {
    TvSeries tvSeries = new TvSeries();
    tvSeries.setId(1L);
    return tvSeries;
  }

}
