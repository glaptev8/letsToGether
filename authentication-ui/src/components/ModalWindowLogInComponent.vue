<template>
  <v-dialog v-model="localDialog" max-width="600px">
    <v-card>
      <LoginFormComponent/>
    </v-card>
  </v-dialog>
</template>

<script>
import LoginFormComponent from '@/components/LoginFormComponent.vue';

export default {
  name: "ModalWindowLogInComponent.vue",
  components: { LoginFormComponent },
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
