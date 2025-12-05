<script setup>
import { GraduationCap, Github, Linkedin } from 'lucide-vue-next';
</script>

<template>
    <footer class="footer footer-horizontal bg-[#DEDED1] dark:bg-[#393E46] text-neutral-content items-center p-4">
        <aside class="grid-flow-col items-center">
            <GraduationCap class="w-6 h-6 mr-2 text-[#393E46] dark:text-[#DFD0B8]" stroke="currentColor" />
            <p class="text-[#393E46] dark:text-[#DFD0B8]">Vue and Springboot Project</p>
        </aside>
        <nav class="grid-flow-col gap-4 place-self-center justify-self-end">
            <a href="https://github.com/Southamut" target="_blank" rel="noopener noreferrer">
                <Github class="w-6 h-6 text-[#393E46] dark:text-[#DFD0B8] cursor-pointer" stroke="currentColor" />
            </a>
            <a href="http://www.linkedin.com/in/punyakit-n" target="_blank" rel="noopener noreferrer">
                <Linkedin class="w-6 h-6 text-[#393E46] dark:text-[#DFD0B8] cursor-pointer" stroke="currentColor" />
            </a>
        </nav>
    </footer>
</template>
