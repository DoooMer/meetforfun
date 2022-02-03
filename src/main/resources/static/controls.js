const signals = new Signals();
const settings = new Settings();
const app = new Vue({
    el: '#app',
    data: {
        loading: true,
        showTitle: true,
        showPlaylist: false,
        isRepeat: true,
        muteInterval: 300,
        trackId: null,
        // playlist (all tracks)
        tracksTotal: 0,
        playlist: [],
        // search by playlist
        search: null,
        // playlist filtered by search (if set)
        tracks: [],
        dnd: false,
        mute: false,
        playState: undefined,
        volume: 1,
    },
    created() {
        // load current state
        this.showTitle = settings.getShowTitle();
        this.showPlaylist = settings.getShowPlaylist();
        this.isRepeat = settings.getRepeat();
        this.muteInterval = settings.getMuteInterval();
        this.trackId = signals.getTrackId();

        // sync not stored state of player
        signals.pushRequestSyncAll();

        // load tracks list
        API.tracks()
            .then(response => {
                this.tracksTotal = response.data.total;
                this.tracks = this.playlist = response.data.tracks;
            })
            .catch(console.error);

        // subscribe for events in storage
        signals.listen(event => {
            switch (event.key) {
                case Signals.CTRL_TRACKID:
                    this.trackId = event.newValue;
                    break;
                case Signals.CTRL_TUNNEL:
                    this.handleCtrl(JSON.parse(event.newValue));
                    break;
            }
        });

        // subscribe for settings in storage
        settings.listen(event => {
            let value = JSON.parse(event.newValue);

            switch (event.key) {
                case Settings.SHOW_TITLE:
                    this.showTitle = value;
                    break;
                case Settings.SHOW_PLAYLIST:
                    this.showPlaylist = value;
                    break;
                case Settings.REPEAT:
                    this.isRepeat = value;
                    break;
                case Settings.MUTE_INTERVAL:
                    this.muteInterval = value;
                    break;
            }
        });

        this.loading = false;
    },
    watch: {
        search: function (newValue) {
            // filtering playlist by text (name)
            if (newValue == null || newValue.length < 1) {
                this.tracks = this.playlist;
                return;
            }

            const search = newValue.toLowerCase();

            this.tracks = this.playlist.filter(track => {
                return track.name.toLowerCase().includes(search);
            });
        },
        trackId: function (newValue) {
            signals.pushTrackId(newValue);
        },
        mute: function (newValue) {
            signals.pushMute(newValue);
        },
        showTitle: function (newValue) {
            settings.setShowTitle(newValue);
        },
        showPlaylist: function (newValue) {
            settings.setShowPlaylist(newValue);
        },
        isRepeat: function (newValue) {
            settings.setRepeat(newValue);
        },
        muteInterval: function (newValue) {
            settings.setMuteInterval(newValue);
        },
        volume: function (newValue) {
            signals.pushVolumeChange(newValue);
        },
    },
    methods: {
        play(id) {
            this.trackId = id;
        },
        handleCtrl(data) {
            switch (data.action) {
                case CtrlEvent.PLAYSTATE:
                    this.playState = data.value;
                    break;
                case CtrlEvent.VOLUMESTATE:
                    this.volume = data.value;
                    break;
                case CtrlEvent.DATASYNCALL:
                    this.volume = data.value.volume;
                    this.mute = data.value.mute;
                    this.playState = data.value.playpause;
                    break;
            }
        },
        next() {
            signals.pushNext();
        },
        playpause() {
            signals.pushPlayPause();
        },
        dnd() {
            this.showTitle = false;
            this.showPlaylist = false;
        },
        toggleMute() {
            this.mute = !this.mute;
        },
    },
});