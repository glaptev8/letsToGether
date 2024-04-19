<template>
  <v-dialog v-model="localDialog" max-width="600px">
    <v-card>
        <ProfileFormComponent/>
    </v-card>
  </v-dialog>
</template>

<script>
import ProfileFormComponent from '@/components/ProfileFormComponent.vue';

export default {
  name: "ModalWindowProfileComponent",
  components: { ProfileFormComponent },
  props: {
    value: Boolean
  },
  computed: {
    localDialog: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('update:value', value);
      }
    }
  }
}
</script>

<style scoped lang="sass">

</style>
