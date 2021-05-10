<template lang="pug">
div
    v-btn.connect-btn(
        fab,
        outlined,
        color="cyan",
        :width="size",
        :height="size",
        :class="{ connecting }",
        :ripple="false",
        @click="connect"
    )
        .white--text
            v-icon(size="64", v-if="connecting") mdi-bluetooth-audio

            span.text-h4(v-else) 
                v-avatar(:size="`calc(${size} / 2.5)`")
                    v-img(src="~/assets/connect.svg")
</template>

<script lang="ts">
import { Vue, Component } from 'nuxt-property-decorator'


@Component
export default class extends Vue {
    size = '30vh'

    get connecting() {
        return this.$accessor.connecting
    }

    get device() {
        return this.$accessor.setting.device
    }

    async connect() {
        if (this.connecting || !this.device) {
            return
        }

        navigator.vibrate(20)

        let success = await $native.latch.connect(this.device.address)

        // if (!success) {
        //     this.snackbar.show = true
        //     this.snackbar.timeout = 1000
        //     this.snackbar.text = '連線失敗'
        //     this.snackbar.color = 'error'
        // }
    }

    mounted() {
        // setInterval(() => {
        //     this.state++;
        //     this.state %= 2
        // }, 1000)

    }
}
</script>

<style lang="scss" scoped>
.connect-btn {
    box-shadow: 0px 0px 30px 10px cyan;
    // border-color: white;
    margin: 0 auto;

    animation-name: pulse;
    animation-duration: 2s;
    animation-delay: 0.25s;
    animation-iteration-count: infinite;

    &:active:not(.connecting) {
        animation: none;
        transform: scale(0.8);
        transition-timing-function: cubic-bezier(0.47, 2.02, 0.31, -0.36);
    }
}

@mixin ripple($delay, $duration) {
    content: "";
    background-color: transparent;
    position: absolute;
    width: inherit;
    height: inherit;
    border: 3px solid cyan;
    border-radius: 50%;
    animation-name: ripple;
    animation-delay: $delay;
    animation-duration: $duration;
    animation-iteration-count: infinite;
    animation-timing-function: ease-out;
}

.connecting {
    animation-name: swing;
    animation-duration: 2s;
    animation-iteration-count: infinite;

    &::before {
        @include ripple(0ms, 2s);
    }

    &::after {
        @include ripple(500ms, 2s);
    }
}

@keyframes ripple {
    from {
        opacity: 0.75;
        transform: scale(1);
    }
    to {
        opacity: 0;
        transform: scale(1.75);
        border-width: 20px;
    }
}
</style>
