const app = new Vue({
    el: '#app',
    data: {
        loading: true,
        showTitle: true,
        showPlaylist: false,
        showSettings: false,
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
    },
    created() {
        // load current state
        this.showTitle = JSON.parse(localStorage.getItem('jwp_showTitle') || 'true');
        this.showPlaylist = JSON.parse(localStorage.getItem('jwp_showPlaylist') || 'false');
        this.showSettings = JSON.parse(localStorage.getItem('jwp_showSettings') || 'false');
        this.isRepeat = JSON.parse(localStorage.getItem('jwp_repeat') || 'true');
        this.muteInterval = JSON.parse(localStorage.getItem('jwp_muteInterval') || '300');
        this.trackId = localStorage.getItem('jwp_ctrl_trackId') || null;

        axios.get('/api/tracks')
            .then(response => {
                this.tracksTotal = response.data.total;
                this.tracks = this.playlist = response.data.tracks;
            })
            .catch(console.error);

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
                    this.trackId = event.newValue;
                    break;
                case 'jwp_ctrl_tunnel':
                    this.handleCtrl(JSON.parse(event.newValue));
                    break;
            }
        };

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
            localStorage.setItem('jwp_ctrl_trackId', newValue);
        },
    },
    methods: {
        play(id) {
            this.trackId = id;
        },
        handleCtrl(data) {
            console.log(data);
        },
        next() {
            localStorage.setItem('jwp_ctrl_tunnel', JSON.stringify({'action': 'next'}));
        },
    },
});