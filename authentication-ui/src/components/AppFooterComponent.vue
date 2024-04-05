<template>
  <v-footer color="grey darken-3" app class="footer-custom">
    <v-col class="text-center white--text" cols="12">
      <v-row justify="center" align="center">
        <v-col cols="12" sm="8" md="6">
          ©2024 Lets Together. Все права защищены.
        </v-col>
      </v-row>
    </v-col>
  </v-footer>
</template>

<script>
export default {
  name: "AppFooter"
}
</script>

<style scoped lang="sass">
.footer-custom
  max-height: 50px !important // Установите желаемую минимальную высоту
  padding: 0 !important
</style>
