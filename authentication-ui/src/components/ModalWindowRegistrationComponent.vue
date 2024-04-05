<template>
  <v-dialog v-model="localDialog" max-width="600px">
    <v-card>
        <RegistrationFormComponent/>
    </v-card>
  </v-dialog>
</template>

<script>
import RegistrationFormComponent from '@/components/RegistrationFormComponent.vue';

export default {
  name: "ModalWindowRegistrationComponent",
  components: { RegistrationFormComponent },
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
