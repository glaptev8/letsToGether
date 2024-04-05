<script>
export default {
  name: 'ActivityCard',
  props: {
    item: {
      type: Object,
      required: true,
    },
  },
};
</script>

<template>
  <v-card class="mx-auto" outlined>
    <v-img :src="item.image" height="200px"></v-img>
    <v-card-title>{{ item.title }}</v-card-title>
    <v-card-text>{{ item.description }}</v-card-text>
  </v-card>
</template>

<style scoped lang="sass">

</style>
