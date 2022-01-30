// global event: space button will pause playback
document
    .addEventListener('keypress', e => {

        if (e.key !== " " || e.target.tagName === "INPUT") {
            return;
        }

        const player = document.getElementById('player');

        if (player.paused) {
            player.play();
        } else {
            player.pause();
        }

        e.stopImmediatePropagation();
        e.preventDefault();
    });

// slow fade playback and pause when volume is low
function fade(period, step, callback) {
    const timer = setInterval(() => {
        const player = document.getElementById('player');

        if (player.volume > step) {
            player.volume -= step;
        } else {
            player.volume = 0;
            callback();
            clearInterval(timer);
        }

        if (player.volume <= step) {
            player.pause();
        }
    }, period);
}

const app = new Vue({
    el: '#app',
    // store,
    data: {
        // loading states
        loadingTrack: true,
        loadingPlaylist: true,
        // display settings form
        showSettings: false,
        // display track title above player
        showTitle: true,
        // display all tracks under player
        showPlaylist: true,
        // timer duration for each fade step
        muteInterval: 300,
        // saved previous volume state
        mute_prev: null,
        // flag is true when fade is activated
        isFade: false,
        // flag is true when repeat is enabled
        isRepeat: true,
        // current track data
        trackUrl: null,
        trackName: null,
        trackId: null,
        // playlist (all tracks)
        tracksTotal: 0,
        playlist: [],
        // do not disturb saved previous state
        dnd_prev: null,
        // search by playlist
        search: null,
        // playlist filtered by search (if set)
        tracks: [],
    },
    created() {
        // load saved state
        this.showTitle = JSON.parse(localStorage.getItem('jwp_showTitle') || 'true');
        this.showPlaylist = JSON.parse(localStorage.getItem('jwp_showPlaylist') || 'false');
        this.showSettings = JSON.parse(localStorage.getItem('jwp_showSettings') || 'false');
        this.isRepeat = JSON.parse(localStorage.getItem('jwp_repeat') || 'true');
        this.muteInterval = JSON.parse(localStorage.getItem('jwp_muteInterval') || '300');

        // get link to current track on load app
        axios.get('/api/playback/current')
            .then(response => {
                this.trackUrl = response.data.downloadUrl;
                this.trackName = response.data.name;
                this.trackId = response.data.id;
            })
            .catch(console.error)
            .finally(() => {
                this.loadingTrack = false;
            })

        if (this.showPlaylist) {
            axios.get('/api/tracks')
                .then(response => {
                    this.tracksTotal = response.data.total;
                    this.tracks = this.playlist = response.data.tracks;
                })
                .catch(console.error)
                .finally(() => {
                    this.loadingPlaylist = false;
                });
        }

        window.onstorage = event => {
            switch (event.key) {
                case 'jwp_showTitle':
                    this.showTitle = JSON.parse(event.newValue);
                    break;
                case 'jwp_showPlaylist':
                    this.showPlaylist = JSON.parse(event.newValue);
                    break;
                case 'jwp_showSettings':
                    this.showSettings = JSON.parse(event.newValue);
                    break;
                case 'jwp_repeat':
                    this.isRepeat = JSON.parse(event.newValue);
                    break;
                case 'jwp_muteInterval':
                    this.muteInterval = JSON.parse(event.newValue);
                    break;
                case 'jwp_ctrl_trackId':
                    this.play(event.newValue);
                    break;
                case 'jwp_ctrl_tunnel':
                    this.handleCtrl(JSON.parse(event.newValue));
                    break;
            }
        };
    },
    watch: {
        showPlaylist: function (newValue, oldValue) {
            // load playlist by toggle setting
            if (newValue) {
                localStorage.setItem('jwp_showPlaylist', JSON.stringify(newValue));
                axios.get('/api/tracks')
                    .then(response => {
                        this.tracksTotal = response.data.total;
                        this.tracks = this.playlist = response.data.tracks;
                    })
                    .catch(console.error)
                    .finally(() => {
                        this.loadingPlaylist = false;
                    })
            }
        },
        showTitle: function (newValue) {
            localStorage.setItem('jwp_showTitle', JSON.stringify(newValue));
        },
        showSettings: function (newValue) {
            localStorage.setItem('jwp_showSettings', JSON.stringify(newValue));
        },
        isRepeat: function (newValue) {
            localStorage.setItem('jwp_repeat', JSON.stringify(newValue));
        },
        muteInterval: function (newValue) {
            localStorage.setItem('jwp_muteInterval', JSON.stringify(newValue));
        },
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
            localStorage.setItem('jwp_ctrl_trackId', newValue);
        },
    },
    computed: {
        loading: function () {
            return this.loadingTrack && (this.showPlaylist && this.loadingPlaylist);
        },
    },
    methods: {
        next() {
            // get link to next track
            axios.get('/api/playback/next')
                .then(response => {
                    this.trackUrl = response.data.downloadUrl;
                    this.trackName = response.data.name;
                    this.trackId = response.data.id;
                })
                .catch(e => {
                    console.error(e);
                    this.next();
                });
        },
        prev() {
            // todo: get link to previous track
        },
        fade() {
            // slow down volume and pause, or set volume to max
            const player = document.getElementById('player');

            // restore volume (max)
            if (player.volume < 0.1 || this.mute_prev !== null) {
                player.volume = this.mute_prev; // restore previous volume level
                this.mute_prev = null;
                return;
            }

            this.mute_prev = player.volume; // remember volume level
            this.isFade = true;

            fade(this.muteInterval, 0.1, () => {
                this.isFade = false
            });
        },
        play(id) {
            // get selected track and play
            axios.get('/api/playback/' + id)
                .then(response => {
                    this.trackUrl = response.data.downloadUrl;
                    this.trackName = response.data.name;
                    this.trackId = response.data.id;
                })
                .catch(console.error);
        },
        dnd() {
            // do not disturb
            if (!this.dnd_prev && (this.showTitle || this.showPlaylist)) {
                // save state
                this.dnd_prev = {
                    showTitle: this.showTitle,
                    showPlaylist: this.showPlaylist,
                };
                // on
                this.showTitle = false;
                this.showPlaylist = false;
                return;
            }

            if (this.showTitle ^ this.showPlaylist) {
                // save state
                this.dnd_prev = {
                    showTitle: this.showTitle,
                    showPlaylist: this.showPlaylist,
                };
                // on
                this.showTitle = false;
                this.showPlaylist = false;
                return;
            }

            // off, back to previous state
            if (this.dnd_prev) {
                this.showTitle = this.dnd_prev.showTitle;
                this.showPlaylist = this.dnd_prev.showPlaylist;
                this.dnd_prev = null;
            } else {
                this.showTitle = true;
                this.showPlaylist = true;
            }

        },
        handleCtrl(data) {
            console.log(data);

            switch (data.action) {
                case 'next':
                    this.next();
                    localStorage.setItem('jwp_ctrl_tunnel', null);
                    break;
            }
        },
    }
});