export interface Post{
    postId:number
    userId:number
    content:string
    mediaUrl?:string
    privacy:string
    likeCount:number
    likedByUsers:string[]
    createdAt:string
    updatedAt:string

}