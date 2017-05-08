package com.simplecity.amp_library.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.simplecity.amp_library.R;
import com.simplecity.amp_library.detail.AlbumDetailFragment;
import com.simplecity.amp_library.detail.ArtistDetailFragment;
import com.simplecity.amp_library.detail.BaseDetailFragment;
import com.simplecity.amp_library.detail.GenreDetailFragment;
import com.simplecity.amp_library.detail.PlaylistDetailFragment;
import com.simplecity.amp_library.model.Album;
import com.simplecity.amp_library.model.AlbumArtist;
import com.simplecity.amp_library.model.Genre;
import com.simplecity.amp_library.model.Playlist;
import com.simplecity.amp_library.search.SearchActivity;
import com.simplecity.amp_library.ui.activities.ToolbarListener;
import com.simplecity.amp_library.ui.adapters.PagerAdapter;
import com.simplecity.amp_library.ui.views.SlidingTabLayout;
import com.simplecity.amp_library.utils.DialogUtils;
import com.simplecity.amp_library.utils.ShuttleUtils;
import com.simplecity.amp_library.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.com.androidnavigation.fragment.FragmentInfo;


public class LibraryController extends BaseFragment implements
        AlbumArtistFragment.AlbumArtistClickListener,
        AlbumFragment.AlbumClickListener,
        SuggestedFragment.SuggestedClickListener,
        PlaylistFragment.PlaylistClickListener,
        GenreFragment.GenreClickListener {

    private static final String TAG = "LibraryController";

    private static final String GENRES_ORDER = "genres_order";
    private static final String SUGGESTED_ORDER = "suggested_order";
    private static final String ARTISTS_ORDER = "artists_order";
    private static final String ALBUMS_ORDER = "albums_order";
    private static final String SONGS_ORDER = "songs_order";
    private static final String FOLDERS_ORDER = "folders_order";
    private static final String PLAYLISTS_ORDER = "playlists_order";

    private static final String SHOW_GENRES = "show_genres";
    private static final String SHOW_SUGGESTED = "show_suggested";
    private static final String SHOW_ARTISTS = "show_artists";
    private static final String SHOW_ALBUMS = "show_albums";
    private static final String SHOW_SONGS = "show_songs";
    private static final String SHOW_FOLDERS = "show_folders";
    private static final String SHOW_PLAYLISTS = "show_playlists";

    private int defaultPage;

    private PagerAdapter adapter;

    @BindView(R.id.tabs)
    SlidingTabLayout slidingTabLayout;

    @BindView(R.id.pager)
    ViewPager pager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static FragmentInfo fragmentInfo() {
        return new FragmentInfo(LibraryController.class, null, "LibraryController");
    }

    public LibraryController() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        boolean showGenres = prefs.getBoolean(SHOW_GENRES, true);
        boolean showSuggested = prefs.getBoolean(SHOW_SUGGESTED, true);
        boolean showArtists = prefs.getBoolean(SHOW_ARTISTS, true);
        boolean showAlbums = prefs.getBoolean(SHOW_ALBUMS, true);
        boolean showSongs = prefs.getBoolean(SHOW_SONGS, true);
        boolean showFolders = prefs.getBoolean(SHOW_FOLDERS, false);
        if (!ShuttleUtils.isUpgraded()) {
            showFolders = false;
        }
        boolean showPlaylists = prefs.getBoolean(SHOW_PLAYLISTS, false);

        int genresOrder = prefs.getInt(GENRES_ORDER, 0);
        int suggestedOrder = prefs.getInt(SUGGESTED_ORDER, 1);
        int artistsOrder = prefs.getInt(ARTISTS_ORDER, 2);
        int albumsOrder = prefs.getInt(ALBUMS_ORDER, 3);
        int songsOrder = prefs.getInt(SONGS_ORDER, 4);
        int foldersOrder = prefs.getInt(FOLDERS_ORDER, 5);
        int playlistsOrder = prefs.getInt(PLAYLISTS_ORDER, 6);

        adapter = new PagerAdapter(getChildFragmentManager());

        for (int i = 0; i < 8; i++) {
            if (genresOrder == i) {
                if (showGenres) {
                    adapter.addFragment(GenreFragment.newInstance(getString(R.string.genres_title)));
                }
            } else if (suggestedOrder == i) {
                if (showSuggested) {
                    adapter.addFragment(SuggestedFragment.newInstance(getString(R.string.suggested_title)));
                }
            } else if (artistsOrder == i) {
                if (showArtists) {
                    adapter.addFragment(AlbumArtistFragment.newInstance(getString(R.string.artists_title)));
                }
            } else if (albumsOrder == i) {
                if (showAlbums) {
                    adapter.addFragment(AlbumFragment.newInstance(getString(R.string.albums_title)));
                }
            } else if (songsOrder == i) {
                if (showSongs) {
                    adapter.addFragment(SongFragment.newInstance(getString(R.string.tracks_title)));
                }
            } else if (foldersOrder == i) {
                if (showFolders) {
                    adapter.addFragment(FolderFragment.newInstance(getString(R.string.folders_title)));
                }
            } else if (playlistsOrder == i) {
                if (showPlaylists) {
                    adapter.addFragment(PlaylistFragment.newInstance(getString(R.string.playlists_title)));
                }
            }
        }

        defaultPage = 2;

        String defaultPage = prefs.getString("pref_default_page", null);
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getPageTitle(i).equals(defaultPage)) {
                this.defaultPage = i;
            }
        }
        if (this.defaultPage > adapter.getCount()) {
            if (adapter.getCount() > 3) {
                this.defaultPage = 2;
            } else {
                this.defaultPage = 0;
            }
        }

        SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, key) -> {
            if (key.equals("pref_theme_highlight_color") || key.equals("pref_theme_accent_color") || key.equals("pref_theme_white_accent")) {
                themeUIComponents();
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_library, container, false);

        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getCount() - 1);
        pager.setCurrentItem(defaultPage);

        ThemeUtils.themeTabLayout(getActivity(), slidingTabLayout);
        slidingTabLayout.setViewPager(pager);

        pager.postDelayed(() -> {
            if (pager != null) {
                DialogUtils.showRateSnackbar(getActivity(), pager);
            }
        }, 1000);

        themeUIComponents();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof ToolbarListener) {
            ((ToolbarListener) getActivity()).toolbarAttached((Toolbar) view.findViewById(R.id.toolbar));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_library, menu);
        setupCastMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
        }
        return false;
    }

    private void openSearch() {
        startActivity(new Intent(getContext(), SearchActivity.class));
    }

    private void themeUIComponents() {

        if (slidingTabLayout != null) {
            ThemeUtils.themeTabLayout(getActivity(), slidingTabLayout);
        }
        if (pager != null) {
            ThemeUtils.themeViewPager(pager);
        }
    }

    @Override
    public void onAlbumArtistClicked(AlbumArtist albumArtist, View transitionView) {
        String transitionName = ViewCompat.getTransitionName(transitionView);
        ArtistDetailFragment detailFragment = ArtistDetailFragment.newInstance(albumArtist, transitionName);
        pushDetailFragment(detailFragment, transitionView);
    }

    @Override
    public void onAlbumClicked(Album album, View transitionView) {
        String transitionName = ViewCompat.getTransitionName(transitionView);
        AlbumDetailFragment detailFragment = AlbumDetailFragment.newInstance(album, transitionName);
        pushDetailFragment(detailFragment, transitionView);
    }

    @Override
    public void onGenreClicked(Genre genre) {
        pushDetailFragment(GenreDetailFragment.newInstance(genre), null);
    }

    @Override
    public void onPlaylistClicked(Playlist playlist) {
        pushDetailFragment(PlaylistDetailFragment.newInstance(playlist), null);
    }

    void pushDetailFragment(BaseDetailFragment detailFragment, @Nullable View transitionView) {

        List<Pair<View, String>> transitions = new ArrayList<>();

        if (transitionView != null) {
            String transitionName = ViewCompat.getTransitionName(transitionView);
            transitions.add(new Pair<>(transitionView, transitionName));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Transition moveTransition = TransitionInflater.from(getContext()).inflateTransition(R.transition.image_transition);
                detailFragment.setSharedElementEnterTransition(moveTransition);
                detailFragment.setSharedElementReturnTransition(moveTransition);
            }
        }

        getNavigationController().pushViewController(detailFragment, "DetailFragment", transitions);
    }

    @Override
    protected String screenName() {
        return "LibraryController";
    }

}