<template lang="pug">
div(style="position: relative")
    v-overlay(absolute, v-if="overlay")
        v-progress-circular(indeterminate, size="72", width="10", color="")
    .padlock-wrap(:style="style")
        .padlock(:class="{ unlock: !locked }", @click="onClick")
            .keyhole
</template>

<script lang="ts">
import { Vue, Component } from 'nuxt-property-decorator'

@Component
export default class extends Vue {
    lockAudio = new Audio(require('~/assets/lock.mp3'))
    unlockAudio = new Audio(require('~/assets/unlock.mp3'))
    innerHeight = window.innerHeight
    waiting = false

    get scale() {
        return (this.innerHeight - 56) * .95 * .5 / 350
    }

    get style() {
        return `
            transform: scale(${this.scale});
            height: ${350 * this.scale}px;
        `
    }

    get latchState() {
        return this.$accessor.latchState
    }

    get overlay() {
        return !(this.locked || this.unlocked) || this.waiting
    }

    get disconnected() {
        return this.$accessor.disconnected
    }

    get locked() {
        return this.$accessor.locked
    }

    get unlocked() {
        return this.$accessor.unlocked
    }

    async onClick() {
        navigator.vibrate(50)
        let state = this.latchState

        this.waiting = true

        if (this.locked) {
            this.unlockAudio.play()
            await $native.latch.unlock()
        } else if (this.unlocked) {
            this.lockAudio.play()
            await $native.latch.lock()
        }

        await new Promise<void>(resolve => {
            let i = setInterval(() => {
                if (state != this.latchState || this.disconnected) {
                    clearInterval(i)
                    resolve()
                }
            }, 100)
        })

        this.waiting = false
    }

    mounted() {
        setInterval(() => this.innerHeight = window.innerHeight, 500)
    }
}
</script>

<style scoped>
.padlock-wrap {
    display: inline-block;
    width: 260px;
    height: 350px;
    transform-origin: bottom center;
}

.padlock {
    position: absolute;
    width: 260px;
    height: 200px;
    background-image: linear-gradient(
        to bottom right,
        #f2bc23 49.9%,
        #eab02a 50%
    );
    border-radius: 20px 20px 100px 100px;
    bottom: 0;
    left: 0;
}

.padlock.unlock::before {
    transform: translate(-50%, -60%);
}

.padlock.unlock::after {
    transform: translate(-50%, calc(-50% - 15px)) rotate(90deg);
}

.padlock::before {
    content: "";
    position: absolute;
    left: 50%;
    z-index: -1;
    width: 200px;
    height: 250px;
    border-radius: 125px;
    border: 40px solid #dbe1e4;
    transform: translate(-50%, -50%);
    transition: transform 300ms cubic-bezier(0.17, 0.67, 0.65, 1.52);
    -webkit-clip-path: polygon(
        0% 0%,
        100% 0%,
        100% 65%,
        50% 65%,
        50% 57%,
        22% 57%,
        22% 51%,
        15% 51%,
        14% 52%,
        14% 53%,
        15% 54%,
        22% 54%,
        18% 57%,
        2% 57%,
        0% 55%
    );
    clip-path: polygon(
        0% 0%,
        100% 0%,
        100% 65%,
        50% 65%,
        50% 57%,
        22% 57%,
        22% 51%,
        15% 51%,
        14% 52%,
        14% 53%,
        15% 54%,
        22% 54%,
        18% 57%,
        2% 57%,
        0% 55%
    );
}

.padlock::after {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    z-index: 1;
    width: 20px;
    height: 100px;
    background-image: linear-gradient(to right, #ccc 49.9%, #aaa 50%);
    border-radius: 10px;
    transform: translate(-50%, calc(-50% - 15px));
    transition: transform 180ms;
}

.keyhole {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 60px;
    height: 60px;
    background-color: #2d3237;
    border-radius: 50%;
    transform: translate(-50%, calc(-50% - 15px));
}

.keyhole::before {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    width: 30px;
    height: 30px;
    background-color: #3d464d;
    border-radius: 50%;
    transform: translate(-50%, -50%);
}

.keyhole::after {
    content: "";
    position: absolute;
    bottom: -30px;
    left: 50%;
    width: 30px;
    height: 35px;
    background-color: inherit;
    transform: translateX(-50%);
}
</style>