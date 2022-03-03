class Signals {

    static CTRL_TRACKID = 'jwp_ctrl_trackId';
    static CTRL_TUNNEL = 'jwp_ctrl_tunnel';

    pushNext() {
        this._push({
            action: CtrlEvent.NEXT,
        });
    }

    pushMute() {
        this._push({
            action: CtrlEvent.MUTE,
        });
    }

    pushPlayState(state) {
        this._push({
            action: CtrlEvent.PLAYSTATE,
            value: state,
        });
    }

    pushVolumeState(volume) {
        this._push({
            action: CtrlEvent.VOLUMESTATE,
            value: volume,
        });
    }

    pushPlayPause() {
        this._push({
            action: CtrlEvent.PLAYPAUSE,
        });
    }

    pushVolumeChange(value) {
        this._push({
            action: CtrlEvent.VOLUMECHANGE,
            value: value,
        });
    }

    pushRequestSyncAll() {
        this._push({
            action: CtrlEvent.SYNCALL,
        });
    }

    pushDataSyncAll(data) {
        this._push({
            action: CtrlEvent.DATASYNCALL,
            value: data,
        });
    }

    pushTrackId(id) {
        localStorage.setItem(Signals.CTRL_TRACKID, id);
    }

    getTrackId() {
        return localStorage.getItem(Signals.CTRL_TRACKID) || null;
    }

    _push(event) {
        localStorage.setItem(Signals.CTRL_TUNNEL, JSON.stringify(event));
    }

    reset() {
        localStorage.setItem(Signals.CTRL_TUNNEL, null);
    }

    listen(callback) {
        window.addEventListener('storage', callback);
    }
}

class Settings {
    static SHOW_TITLE = 'jwp_showTitle';
    static SHOW_PLAYLIST = 'jwp_showPlaylist';
    static SHOW_SETTINGS = 'jwp_showSettings';
    static SHOW_MEME = 'jwp+showMeme';
    static REPEAT = 'jwp_repeat';
    static MUTE_INTERVAL = 'jwp_muteInterval';

    getShowTitle() {
        return JSON.parse(localStorage.getItem(Settings.SHOW_TITLE) || 'true');
    }

    setShowTitle(value) {
        localStorage.setItem(Settings.SHOW_TITLE, JSON.stringify(value));
    }

    getShowPlaylist() {
        return JSON.parse(localStorage.getItem(Settings.SHOW_PLAYLIST) || 'false');
    }

    setShowPlaylist(value) {
        localStorage.setItem(Settings.SHOW_PLAYLIST, JSON.stringify(value));
    }

    getShowSettings() {
        return JSON.parse(localStorage.getItem(Settings.SHOW_SETTINGS) || 'false');
    }

    setShowSettings(value) {
        localStorage.setItem(Settings.SHOW_SETTINGS, JSON.stringify(value));
    }

    getShowMeme() {
        return JSON.parse(localStorage.getItem(Settings.SHOW_MEME) || 'false');
    }

    setShowMeme(value) {
        localStorage.setItem(Settings.SHOW_MEME, JSON.stringify(value));
    }

    getRepeat() {
        return JSON.parse(localStorage.getItem(Settings.REPEAT) || 'true');
    }

    setRepeat(value) {
        localStorage.setItem(Settings.REPEAT, JSON.stringify(value));
    }

    getMuteInterval() {
        return JSON.parse(localStorage.getItem(Settings.MUTE_INTERVAL) || '300');
    }

    setMuteInterval(value) {
        localStorage.setItem(Settings.MUTE_INTERVAL, JSON.stringify(value));
    }

    listen(callback) {
        window.addEventListener('storage', event => {

            if (Settings.events().includes(event.key)) {
                callback(event);
            }

        });
    }

    static events() {
        return [
            Settings.SHOW_TITLE,
            Settings.SHOW_PLAYLIST,
            Settings.SHOW_SETTINGS,
            Settings.SHOW_MEME,
            Settings.REPEAT,
            Settings.MUTE_INTERVAL,
        ];
    }
}

class API {

    static tracks() {
        return axios.get('/api/tracks');
    }

    static playbackCurrent() {
        return axios.get('/api/playback/current');
    }

    static playbackNext() {
        return axios.get('/api/playback/next');
    }

    static playbackId(id) {
        return axios.get('/api/playback/' + id);
    }

}

class MemeAPI {

    static random() {
        return axios.get('https://meme-api.herokuapp.com/gimme');
    }

}

class CtrlEvent {

    static NEXT = 'next';
    static PLAYPAUSE = 'playpause';
    static VOLUMECHANGE = 'volumeChange';
    static MUTE = 'mute';
    static DND = 'dnd';
    static SYNCALL = 'syncAll';
    static PLAYSTATE = 'playState';
    static VOLUMESTATE = 'volumeState';
    static DATASYNCALL = 'dataSyncAll';

}

class MemesProvider {

    timer;

    run(callback, timeout = 5000) {
        this.timer = setInterval(function () {
            MemeAPI.random()
                .then(response => {
                    callback(response.data);
                })
                .catch(console.error);
        }, timeout);
    }

    stop() {
        if (this.timer) {
            clearInterval(this.timer);
        }
    }

}